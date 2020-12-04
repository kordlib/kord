package dev.kord.rest.request

import dev.kord.rest.json.optional
import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.rest.ratelimit.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.TextContent
import io.ktor.http.content.*
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.time.Clock

internal val jsonDefault = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

/**
 * A [RequestHandler] that uses ktor's [HttpClient][client] to execute requests and accepts a [requestRateLimiter]
 * to schedule requests.
 *
 * @param client A [HttpClient] configured with the required headers for identification.
 * @param clock A [Clock] to calculate bucket reset times, exposed for testing.
 * @param parser Serializer used to parse payloads.
 */
@Suppress("EXPERIMENTAL_API_USAGE")
class KtorRequestHandler(
        private val client: HttpClient,
        private val requestRateLimiter: RequestRateLimiter = ExclusionRequestRateLimiter(),
        private val clock: Clock = Clock.systemUTC(),
        private val parser: Json = jsonDefault,
) : RequestHandler {
    private val logger = KotlinLogging.logger("[R]:[KTOR]:[${requestRateLimiter.javaClass.simpleName}]")

    override tailrec suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        val response = requestRateLimiter.consume(request) {
            val httpRequest = client.createRequest(request)
            val response = httpRequest.execute()

            it.complete(RequestResponse.from(response, clock))

            response
        }

        val body = response.readText()
        return when {
            response.isRateLimit -> {
                logger.debug { response.logString(body) }
                handle(request)
            }
            response.isError -> {
                logger.debug { response.logString(body) }
                throw KtorRequestException(response, parser.decodeFromString(DiscordErrorResponse.serializer().optional, body))
            }
            else -> {
                logger.debug { response.logString(body) }
                parser.decodeFromString(request.route.strategy, body)
            }
        }
    }

    private suspend fun <B : Any, R> HttpClient.createRequest(request: Request<B, R>) = request<HttpStatement> {
        method = request.route.method
        headers.appendAll(request.headers)

        url {
            url.takeFrom(dev.kord.rest.route.Route.baseUrl)
            encodedPath += request.path
            parameters.appendAll(request.parameters)
        }

        when (request) {
            is JsonRequest -> run {
                val requestBody = request.body ?: return@run
                val json = parser.encodeToString(requestBody.strategy, requestBody.body)
                logger.debug { request.logString(json) }
                this.body = TextContent(json, io.ktor.http.ContentType.Application.Json)
            }
            is MultipartRequest -> {
                val content = request.data
                this.body = MultiPartFormDataContent(content)
                logger.debug {
                    val json = content.filterIsInstance<PartData.FormItem>()
                            .firstOrNull { it.name == "payload_json" }?.value
                    request.logString(json ?: "")
                }
            }
        }

    }

    companion object {

        operator fun invoke(
                token: String,
                requestRateLimiter: RequestRateLimiter = ExclusionRequestRateLimiter(),
                clock: Clock = Clock.systemUTC(),
                parser: Json = jsonDefault,
        ): KtorRequestHandler {
            val client = HttpClient(CIO) {
                expectSuccess = false
                defaultRequest { header("Authorization", "Bot $token") }
            }
            return KtorRequestHandler(client, requestRateLimiter, clock, parser)
        }
    }

}

fun RequestResponse.Companion.from(response: HttpResponse, clock: Clock): RequestResponse {
    val bucket = response.bucket
    val rateLimit = run {
        val total = Total(response.rateLimitTotal ?: return@run null)
        val remaining = Remaining(response.rateLimitRemaining ?: return@run null)
        RateLimit(total, remaining)
    }

    val reset = Reset(response.channelResetPoint(clock))

    return when {
        response.isGlobalRateLimit -> RequestResponse.GlobalRateLimit(bucket, rateLimit, reset)
        response.isRateLimit -> RequestResponse.BucketRateLimit(bucket
                ?: BucketKey("missing"), rateLimit, reset)
        response.isError -> RequestResponse.Error
        else -> RequestResponse.Accepted(bucket, rateLimit, reset)
    }
}
