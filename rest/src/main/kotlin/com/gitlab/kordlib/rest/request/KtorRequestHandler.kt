package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.ratelimit.*
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.readText
import io.ktor.content.TextContent
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KotlinLogging
import java.time.Clock

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
        private val parser: Json = Json(JsonConfiguration(encodeDefaults = false, strictMode = false))
) : RequestHandler {
    private val logger = KotlinLogging.logger("[R]:[KTOR]:[${requestRateLimiter.javaClass.simpleName}]")

    override tailrec suspend fun <B : Any, R> handle(request: Request<B, R>): R {
        logger.trace { request.logString }

        val response = requestRateLimiter.consume(request) {
            val httpRequest = client.createRequest(request)
            val response = httpRequest.execute()

            it.complete(RequestResponse.from(response, clock))

            logger.trace { response.logString }

            response
        }

        return when {
            response.isRateLimit -> handle(request)
            response.isError -> throw KtorRequestException(response, response.errorString())
            else -> parser.parse(request.route.strategy, response.readText())
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
                    headers.append("payload_json", parser.stringify(it.strategy, it.body))
                    this.body = io.ktor.client.request.forms.MultiPartFormDataContent(request.data)
                }

                is JsonRequest<*, *> -> {
                    val json = parser.stringify(it.strategy, it.body)
                    this.body = TextContent(json, io.ktor.http.ContentType.Application.Json)
                }
            }
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
        response.isError -> RequestResponse.Error(bucket, rateLimit, reset)
        else -> RequestResponse.Accepted(bucket, rateLimit, reset)
    }
}
