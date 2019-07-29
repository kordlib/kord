package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.common.Platform
import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestException
import com.gitlab.kordlib.rest.request.RequestIdentifier
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.call.receive
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readBytes
import io.ktor.http.takeFrom
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class ExclusionRequestHandler(private val client: HttpClient) : RequestHandler {

    private var globalSuspensionPoint = 0L

    private val routeSuspensionPoints = mutableMapOf<RequestIdentifier, Long>()

    //https://discordapp.com/developers/docs/topics/rate-limits#rate-limits
    //there's no real known rate limit, and Discord won't tell us, so this is nothing more than an assumption
    private val emojiRateLimiter = BucketRateLimiter(1, 250)

    private val mutex = Mutex()

    override tailrec suspend fun <T> handle(request: Request<T>): HttpResponse {

        val builder = HttpRequestBuilder().apply {
            url.takeFrom(Route.baseUrl)
            with(request) { apply() }
        }

        val response = mutex.withLock {
            suspendFor(request)

            logger.trace { "REQUEST: ${request.logString}" }

            val response = client.call(builder).receive<HttpResponse>()

            logger.trace { response.logString }

            if (response.isGlobalRateLimit) {
                logger.trace { "GLOBAL RATE LIMIT: ${request.logString}" }
                globalSuspensionPoint = response.globalSuspensionPoint
            }

            if (response.isChannelRateLimit) {
                logger.trace { "ROUTE RATE LIMIT: ${request.logString}" }
                routeSuspensionPoints[request.identifier] = response.channelSuspensionPoint
            }

            response
        }

        if (response.isRateLimit) {
            return handle(request)
        }

        if (response.isError) {
            throw RequestException(response, response.errorString())
        }

        return response
    }

    private suspend fun suspendFor(request: Request<*>) {
        delay(globalSuspensionPoint - Platform.nowMillis())
        if (request.route.path.contains("emoji")) { //https://discordapp.com/developers/docs/topics/rate-limits#rate-limits
            emojiRateLimiter.consume()
        } else {
            val key = request.identifier
            val routeSuspensionPoint = routeSuspensionPoints[key]

            if (routeSuspensionPoint != null) {
                delay(routeSuspensionPoint - Platform.nowMillis())
                routeSuspensionPoints.remove(key)
            }
        }

    }

    private companion object {
        const val rateLimitGlobalHeader = "X-RateLimit-Global"
        const val retryAfterHeader = "Retry-After"
        const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
        const val resetTimeHeader = "X-RateLimit-Reset"

        val HttpResponse.channelSuspensionPoint get() = headers[resetTimeHeader]?.toLong() ?: 0
        val HttpResponse.isRateLimit get() = status.value == 429
        val HttpResponse.isError get() = status.value in 400 until 600
        val HttpResponse.isGlobalRateLimit get() = headers[rateLimitGlobalHeader]?.toBoolean() == true
        val HttpResponse.isChannelRateLimit get() = headers[rateLimitRemainingHeader]?.toIntOrNull() == 0
        val HttpResponse.globalSuspensionPoint
            get() = headers[retryAfterHeader]?.toLong()?.let { it + responseTime.timestamp } ?: 0

        val HttpResponse.logString get() = "$status: ${call.request.method.value} ${call.request.url}"

        suspend fun HttpResponse.errorString(): String {
            val message = String(this.readBytes())
            return if (message.isBlank()) logString
            else "$logString $message"
        }

        val Request<*>.logString
            get() : String {
                val method = route.method.value
                val path = route.path
                val params = routeParams.entries
                        .joinToString(",", "[", "]") { (key, value) -> "$key=$value" }

                return "route: $method/$path params: $params"
            }
    }

}
