package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.json.optional
import com.gitlab.kordlib.rest.json.response.DiscordErrorResponse
import com.gitlab.kordlib.rest.ratelimit.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.*
import io.ktor.content.TextContent
import io.ktor.http.takeFrom
import kotlinx.serialization.builtins.nullable
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
        private val parser: Json = jsonDefault
) : RequestHandler {
    private val logger = KotlinLogging.logger("[R]:[KTOR]:[${requestRateLimiter.javaClass.simpleName}]")

    override tailrec suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        val response = requestRateLimiter.consume(request) {
            val httpRequest = client.createRequest(request)
            val response = httpRequest.execute()

            it.complete(RequestResponse.from(response, clock))

            response
        }

        return when {
            response.isRateLimit -> handle(request)
            response.isError -> {
                throw KtorRequestException(response, Json.decodeFromString(DiscordErrorResponse.serializer().optional, String(response.readBytes())))
            }
            else -> parser.decodeFromString(request.route.strategy, response.readText())
        }
    }

    private suspend fun <B : Any, R> HttpClient.createRequest(request: Request<B, R>) = request<HttpStatement> {
        method = request.route.method
        headers.append("X-RateLimit-Precision", "millisecond")
        headers.appendAll(request.headers)

        url {
            url.takeFrom(com.gitlab.kordlib.rest.route.Route.baseUrl)
            encodedPath += request.path
            parameters.appendAll(request.parameters)
        }

        request.body?.let {
            when (request) {
                is MultipartRequest<*, *> -> {
                    headers.append("payload_json", parser.encodeToString(it.strategy, it.body))
                    this.body = MultiPartFormDataContent(request.data)
                }

                is JsonRequest<*, *> -> {
                    val json = parser.encodeToString(it.strategy, it.body)
                    this.body = TextContent(json, io.ktor.http.ContentType.Application.Json)
                }
            }
        }
    }

    companion object {

        operator fun invoke(
                token: String,
                requestRateLimiter: RequestRateLimiter = ExclusionRequestRateLimiter(),
                clock: Clock = Clock.systemUTC(),
                parser: Json = jsonDefault
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
