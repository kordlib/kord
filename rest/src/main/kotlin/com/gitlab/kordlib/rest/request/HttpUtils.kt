@file:Suppress("unused")

package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.ratelimit.BucketKey
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import java.time.Clock
import java.time.Duration
import java.time.Instant

private const val rateLimitGlobalHeader = "X-RateLimit-Global"
private const val retryAfterHeader = "Retry-After"
private const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
private const val resetTimeHeader = "X-RateLimit-Reset"
private const val bucketRateLimitKey = "X-RateLimit-Bucket"
private const val rateLimit = "X-RateLimit-Limit"
private const val rateLimitResetAfter = "X-RateLimit-Reset-After"

val HttpResponse.channelResetPoint: Instant
    get() {
        val unixSeconds = headers[resetTimeHeader]?.toDouble() ?: return Instant.now()
        return Instant.ofEpochMilli(unixSeconds.times(1000).toLong())
    }

fun HttpResponse.channelResetPoint(clock: Clock) : Instant {
    val seconds = headers[rateLimitResetAfter]?.toDouble() ?: return clock.instant()
    return clock.instant().plus(Duration.ofMillis(seconds.times(1000).toLong()))
}

val HttpResponse.isRateLimit get() = status.value == 429
val HttpResponse.isError get() = status.value in 400 until 600
val HttpResponse.isErrorWithRateLimit get() = status.value == 403 || status.value == 401
val HttpResponse.isGlobalRateLimit get() = headers[rateLimitGlobalHeader] != null
val HttpResponse.rateLimitTotal get() = headers[rateLimit]?.toLongOrNull()
val HttpResponse.rateLimitRemaining get() = headers[rateLimitRemainingHeader]?.toLongOrNull()
val HttpResponse.isChannelRateLimit get() = headers[rateLimitRemainingHeader]?.toIntOrNull() == 0
val HttpResponse.bucket: BucketKey? get() = headers[bucketRateLimitKey]?.let { BucketKey(it) }

/**
 * The unix time (in ms) when the global rate limit gets reset.
 */
fun HttpResponse.globalSuspensionPoint(clock: Clock): Long {
    val msWait = headers[retryAfterHeader]?.toLong() ?: return 0
    return msWait + clock.millis()
}

val HttpResponse.logString get() = "[RESPONSE]:${status.value}:${call.request.method.value}:${call.request.url}"

suspend fun HttpResponse.errorString(): String {
    val message = String(this.readBytes())
    return if (message.isBlank()) logString
    else "$logString $message"
}

val Request<*, *>.logString
    get() : String {
        val method = route.method.value
        val path = route.path
        val params = routeParams.entries
                .joinToString(",", "[", "]") { (key, value) -> "$key=$value" }

        return "[REQUEST]:$method:$path params:$params"
    }