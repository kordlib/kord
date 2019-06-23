package com.gitlab.hopebaron.rest.ratelimit

import com.gitlab.hopebaron.common.Platform
import com.gitlab.hopebaron.rest.request.Request
import com.gitlab.hopebaron.rest.request.RequestIdentifier
import io.ktor.client.response.HttpResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging

interface RequestHandler {

    suspend fun handle(request: Request<*>): HttpResponse

}

private val exclusionRequestLogger = KotlinLogging.logger { }

class ExclusionRequestHandler : RequestHandler {

    private var globalSuspensionPoint = 0L

    private val routeSuspensionPoints = mutableMapOf<RequestIdentifier, Long>()

    private val mutex = Mutex()

    override suspend fun handle(request: Request<*>): HttpResponse = mutex.withLock {
        suspendFor(request)

        val response: HttpResponse = TODO()

        if (response.isGlobalRateLimit) { //request denied & rate limited, try again
            val suspensionPoint = response.globalSuspensionPoint
            exclusionRequestLogger.trace { "request for ${request.identifier} hit global rate limit, retrying at $suspensionPoint" }
            globalSuspensionPoint = suspensionPoint
        }

        if (response.isChannelRateLimit) {
            routeSuspensionPoints[request.identifier] = response.channelSuspensionPoint
        }

        if (response.isRateLimit) {
            return@withLock handle(request)
        }

        response
    }

    private suspend fun suspendFor(request: Request<*>) {
        delay(globalSuspensionPoint - Platform.nowMillis())
        val routSuspensionPoint = routeSuspensionPoints[request.identifier] ?: 0
        delay(routSuspensionPoint - Platform.nowMillis())

    }

    private companion object {
        const val rateLimitGlobalHeader = "X-RateLimit-Global"
        const val retryAfterHeader = "Retry-After"
        const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
        const val resetTimeHeader = "X-RateLimit-Reset"

        val HttpResponse.channelSuspensionPoint get() = headers[resetTimeHeader]?.toLong() ?: 0

        val HttpResponse.isRateLimit get() = status.value == 429
        val HttpResponse.isGlobalRateLimit get() = headers[rateLimitGlobalHeader]?.toBoolean() == true
        val HttpResponse.isChannelRateLimit get() = headers[rateLimitRemainingHeader]?.toInt() == 0
        val HttpResponse.globalSuspensionPoint
            get() = headers[retryAfterHeader]?.toLong()?.let { it + responseTime.timestamp } ?: 0
    }

}
