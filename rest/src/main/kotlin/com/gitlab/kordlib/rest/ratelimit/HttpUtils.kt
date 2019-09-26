package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.rest.request.Request
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readBytes
import java.time.Clock

const val rateLimitGlobalHeader = "X-RateLimit-Global"
const val retryAfterHeader = "Retry-After"
const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
const val resetTimeHeader = "X-RateLimit-Reset"

/**
 * The unix time (in ms) when the rate limit for this endpoint gets reset
 */
val HttpResponse.channelSuspensionPoint: Long
    get() {
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
fun HttpResponse.globalSuspensionPoint(clock: Clock): Long {
    val msWait = headers[retryAfterHeader]?.toLong() ?: return 0
    return msWait + clock.millis()
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