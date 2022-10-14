package dev.kord.rest.request

import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.rest.ratelimit.*
import dev.kord.rest.route.optional
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.TextContent
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import mu.KotlinLogging

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
public class KtorRequestHandler(
    private val client: HttpClient,
    private val requestRateLimiter: RequestRateLimiter = ExclusionRequestRateLimiter(),
    private val clock: Clock = Clock.System,
    private val parser: Json = jsonDefault,
    override val token: String
) : RequestHandler {
    private val logger = KotlinLogging.logger("[R]:[KTOR]:[${requestRateLimiter.javaClass.simpleName}]")

    override tailrec suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        val response = requestRateLimiter.consume(request) {
            val httpRequest = client.createRequest(request)
            val response = httpRequest.execute()

            it.complete(RequestResponse.from(response, clock))

            response
        }

        val body = response.bodyAsText()
        return when {
            response.isRateLimit -> {
                logger.debug { response.logString(body) }
                handle(request)
            }
            response.isError -> {
                logger.debug { response.logString(body) }
                if (response.contentType() == ContentType.Application.Json)
                    throw KtorRequestException(
                        response, request, DiscordErrorResponse.serializer().optional.deserialize(parser, body)
                    )
                else throw KtorRequestException(response, request, null)
            }
            else -> {
                logger.debug { response.logString(body) }
                request.route.mapper.deserialize(parser, body)
            }
        }
    }

    private suspend fun <B : Any, R> HttpClient.createRequest(request: Request<B, R>) = prepareRequest {
        method = request.route.method
        headers.appendAll(request.headers)

        url {
            url.takeFrom(request.baseUrl)
            encodedPath += request.path
            parameters.appendAll(request.parameters)
        }

        when (request) {
            is JsonRequest -> run {
                val requestBody = request.body ?: return@run
                val json = parser.encodeToString(requestBody.strategy, requestBody.body)
                logger.debug { request.logString(json) }
                setBody(TextContent(json, ContentType.Application.Json))
            }
            is MultipartRequest -> {
                val content = request.data
                setBody(MultiPartFormDataContent(content))
                logger.debug {
                    val json = content.filterIsInstance<PartData.FormItem>()
                        .firstOrNull { it.name == "payload_json" }?.value
                    request.logString(json ?: "")
                }
            }
        }

    }
}


public fun KtorRequestHandler(
    token: String,
    requestRateLimiter: RequestRateLimiter = ExclusionRequestRateLimiter(),
    clock: Clock = Clock.System,
    parser: Json = jsonDefault,
): KtorRequestHandler {
    val client = HttpClient(CIO) {
        expectSuccess = false
    }
    return KtorRequestHandler(client, requestRateLimiter, clock, parser, token)
}

public fun RequestResponse.Companion.from(response: HttpResponse, clock: Clock): RequestResponse {
    val bucket = response.bucket
    val rateLimit = run {
        val total = Total(response.rateLimitTotal ?: return@run null)
        val remaining = Remaining(response.rateLimitRemaining ?: return@run null)
        RateLimit(total, remaining)
    }

    val reset = Reset(response.channelResetPoint(clock))

    return when {
        response.isGlobalRateLimit -> RequestResponse.GlobalRateLimit(bucket, rateLimit, reset)
        response.isRateLimit && bucket != null -> RequestResponse.BucketRateLimit(bucket, rateLimit, reset)
        response.isRateLimit -> RequestResponse.UnknownBucketRateLimit(rateLimit, reset)
        response.isError -> RequestResponse.Error
        else -> RequestResponse.Accepted(bucket, rateLimit, reset)
    }
}
