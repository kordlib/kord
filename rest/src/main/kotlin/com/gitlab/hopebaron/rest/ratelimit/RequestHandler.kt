package com.gitlab.hopebaron.rest.ratelimit

import com.gitlab.hopebaron.common.Platform
import com.gitlab.hopebaron.rest.request.Request
import com.gitlab.hopebaron.rest.request.RequestIdentifier
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.call.receive
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.parameter
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.takeFrom
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging

interface RequestHandler {

    suspend fun <T> handle(request: Request<T>): HttpResponse

}

private val exclusionRequestLogger = KotlinLogging.logger { }

class ExclusionRequestHandler(private val client: HttpClient) : RequestHandler {

    private var globalSuspensionPoint = 0L

    private val routeSuspensionPoints = mutableMapOf<RequestIdentifier, Long>()

    private val mutex = Mutex()

    override suspend fun <T> handle(request: Request<T>): HttpResponse {

        val builder = HttpRequestBuilder().apply {
            url.takeFrom(Route.baseUrl)
            with(request) { apply() }
        }

        println(builder.url.buildString())

        val response = mutex.withLock {
            suspendFor(request.identifier)

            val response = client.call(builder).receive<HttpResponse>()

            if (response.isGlobalRateLimit) {
                val suspensionPoint = response.globalSuspensionPoint
                exclusionRequestLogger.trace { "request for ${request.identifier} hit global rate limit, retrying at $suspensionPoint" }
                globalSuspensionPoint = suspensionPoint
            }

            if (response.isChannelRateLimit) {
                routeSuspensionPoints[request.identifier] = response.channelSuspensionPoint
            }

            response
        }

        if (response.isRateLimit) {
            return handle(request)
        }

        if (response.isError) {
            //TODO, throw RequestException
            throw error(response.readText())
        }

        return response
    }

    private suspend fun suspendFor(identifier: RequestIdentifier) {
        delay(globalSuspensionPoint - Platform.nowMillis())
        val routSuspensionPoint = routeSuspensionPoints[identifier] ?: 0
        delay(routSuspensionPoint - Platform.nowMillis())

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
        val HttpResponse.isChannelRateLimit get() = headers[rateLimitRemainingHeader]?.toInt() == 0
        val HttpResponse.globalSuspensionPoint
            get() = headers[retryAfterHeader]?.toLong()?.let { it + responseTime.timestamp } ?: 0
    }

}
