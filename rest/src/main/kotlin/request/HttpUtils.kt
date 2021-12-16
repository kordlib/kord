@file:Suppress("unused")

package dev.kord.rest.request

import dev.kord.rest.ratelimit.BucketKey
import io.ktor.client.statement.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

private const val rateLimitGlobalHeader = "X-RateLimit-Global"
private const val retryAfterHeader = "Retry-After"
private const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
private const val resetTimeHeader = "X-RateLimit-Reset"
private const val bucketRateLimitKey = "X-RateLimit-Bucket"
private const val rateLimit = "X-RateLimit-Limit"
private const val rateLimitResetAfter = "X-RateLimit-Reset-After"
private const val auditLogReason = "X-Audit-Log-Reason"

/**
 * Sets the reason that will show up in the [Discord Audit Log]() to [reason] for this request.
 */
fun <T> RequestBuilder<T>.auditLogReason(reason: String?) = reason?.let { header(auditLogReason, reason) }

val HttpResponse.channelResetPoint: Instant
    get() {
        val unixSeconds = headers[resetTimeHeader]?.toDouble() ?: return Clock.System.now()
        return Instant.fromEpochMilliseconds(unixSeconds.times(1000).toLong())
    }

fun HttpResponse.channelResetPoint(clock: Clock): Instant {
    val seconds = headers[rateLimitResetAfter]?.toDouble() ?: return clock.now()
    return clock.now().plus(seconds.seconds)
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
    val secondsWait = headers[retryAfterHeader]?.toLong() ?: return clock.now().toEpochMilliseconds()
    return (secondsWait * 1000) + clock.now().toEpochMilliseconds()
}

fun HttpResponse.logString(body: String) =
    "[RESPONSE]:${status.value}:${call.request.method.value}:${call.request.url} body:$body"

suspend fun HttpResponse.errorString(): String {
    val message = String(this.readBytes())
    return logString(message)
}

fun Request<*, *>.logString(body: String): String {
    val method = route.method.value
    val path = route.path
    val params = routeParams.entries
        .joinToString(",", "[", "]") { (key, value) -> "$key=$value" }

    return "[REQUEST]:$method:$path params:$params body:$body"
}
