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
import java.time.Duration

private val logger = KotlinLogging.logger {}

class ExclusionRequestHandler(private val client: HttpClient) : RequestHandler {

    private var globalSuspensionPoint = 0L

    private val routeSuspensionPoints = mutableMapOf<RequestIdentifier, Long>()

    private val mutex = Mutex()

    private val autoBanRateLimiter = BucketRateLimiter(25000, Duration.ofMinutes(10))

    override tailrec suspend fun <T> handle(request: Request<T>): HttpResponse {

        val builder = HttpRequestBuilder().apply {
            headers.append("X-RateLimit-Precision", "millisecond")
            url.takeFrom(Route.baseUrl)
            with(request) { apply() }
        }

        val response = mutex.withLock {
            suspendFor(request)

            logger.trace { "REQUEST: ${request.logString}" }

            val response = client.call(builder).receive<HttpResponse>()

            logger.trace { response.logString }

            if (response.isGlobalRateLimit) {
                logger.trace { "GLOBAL RATE LIMIT UNTIL ${response.globalSuspensionPoint}: ${request.logString}" }
                globalSuspensionPoint = response.globalSuspensionPoint
            }

            if (response.isChannelRateLimit) {
                logger.trace { "ROUTE RATE LIMIT UNTIL ${response.channelSuspensionPoint}: ${request.logString}" }
                routeSuspensionPoints[request.identifier] = response.channelSuspensionPoint
            }

            response
        }

        if (response.isRateLimit) {
            autoBanRateLimiter.consume()
            return handle(request)
        }

        if (response.isErrorWithRateLimit) {
            autoBanRateLimiter.consume()
        }

        if (response.isError) {
            throw RequestException(response, response.errorString())
        }

        return response
    }

    private suspend fun suspendFor(request: Request<*>) {
        delay(globalSuspensionPoint - Platform.nowMillis())

            val key = request.identifier
            val routeSuspensionPoint = routeSuspensionPoints[key]

            if (routeSuspensionPoint != null) {
                delay(routeSuspensionPoint - Platform.nowMillis())
                routeSuspensionPoints.remove(key)
            }
    }

    private companion object {
        const val rateLimitGlobalHeader = "X-RateLimit-Global"
        const val retryAfterHeader = "Retry-After"
        const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
        const val resetTimeHeader = "X-RateLimit-Reset"

        /**
         * The unix time (in ms) when the rate limit for this endpoint gets reset
         */
        val HttpResponse.channelSuspensionPoint: Long get() {
            val unixSeconds = headers[resetTimeHeader]?.toDouble() ?: return 0
            return (unixSeconds * 1000).toLong()
        }

        val HttpResponse.isRateLimit get() = status.value == 429
        val HttpResponse.isError get() = status.value in 400 until 600
        val HttpResponse.isErrorWithRateLimit get() = status.value == 403 || status.value == 401
        val HttpResponse.isGlobalRateLimit get() = headers[rateLimitGlobalHeader]?.toBoolean() == true
        val HttpResponse.isChannelRateLimit get() = headers[rateLimitRemainingHeader]?.toIntOrNull() == 0

        /**
         * The unix time (in ms) when the global rate limit gets reset
         */
        val HttpResponse.globalSuspensionPoint: Long
            get() {
                val msWait = headers[retryAfterHeader]?.toLong() ?: return 0
                return msWait + responseTime.timestamp
            }

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