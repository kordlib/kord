package com.gitlab.kordlib.rest.ratelimit

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
import java.time.Clock
import kotlin.time.minutes

private val logger = KotlinLogging.logger {}

class ExclusionRequestHandler(private val client: HttpClient, private val clock: Clock = Clock.systemUTC()) : RequestHandler {

    private var globalSuspensionPoint = 0L

    private val routeSuspensionPoints = mutableMapOf<RequestIdentifier, Long>()

    private val mutex = Mutex()

    private val autoBanRateLimiter = BucketRateLimiter(25000, 10.minutes)

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
                logger.trace { "GLOBAL RATE LIMIT UNTIL ${response.globalSuspensionPoint(clock)}: ${request.logString}" }
                globalSuspensionPoint = response.globalSuspensionPoint(clock)
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
        delay(globalSuspensionPoint - clock.millis())
        globalSuspensionPoint = 0

        val key = request.identifier
        val routeSuspensionPoint = routeSuspensionPoints[key]

        if (routeSuspensionPoint != null) {
            delay(routeSuspensionPoint - clock.millis())
            routeSuspensionPoints.remove(key)
        }
    }
}