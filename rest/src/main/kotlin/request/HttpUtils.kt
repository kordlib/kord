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
public fun <T> RequestBuilder<T>.auditLogReason(reason: String?) {
    reason?.let { urlEncodedHeader(auditLogReason, reason) }
}

public val HttpResponse.channelResetPoint: Instant
    get() {
        val unixSeconds = headers[resetTimeHeader]?.toDouble() ?: return Clock.System.now()
        return Instant.fromEpochMilliseconds(unixSeconds.times(1000).toLong())
    }

public fun HttpResponse.channelResetPoint(clock: Clock): Instant {
    val seconds = headers[rateLimitResetAfter]?.toDouble() ?: return clock.now()
    return clock.now().plus(seconds.seconds)
}

public val HttpResponse.isRateLimit: Boolean get() = status.value == 429
public val HttpResponse.isError: Boolean get() = status.value in 400 until 600
public val HttpResponse.isErrorWithRateLimit: Boolean get() = status.value == 403 || status.value == 401
public val HttpResponse.isGlobalRateLimit: Boolean get() = headers[rateLimitGlobalHeader] != null
public val HttpResponse.rateLimitTotal: Long? get() = headers[rateLimit]?.toLongOrNull()
public val HttpResponse.rateLimitRemaining: Long? get() = headers[rateLimitRemainingHeader]?.toLongOrNull()
public val HttpResponse.isChannelRateLimit: Boolean get() = headers[rateLimitRemainingHeader]?.toIntOrNull() == 0
public val HttpResponse.bucket: BucketKey? get() = headers[bucketRateLimitKey]?.let { BucketKey(it) }

/**
 * The unix time (in ms) when the global rate limit gets reset.
 */
public fun HttpResponse.globalSuspensionPoint(clock: Clock): Long {
    val secondsWait = headers[retryAfterHeader]?.toLong() ?: return clock.now().toEpochMilliseconds()
    return (secondsWait * 1000) + clock.now().toEpochMilliseconds()
}

public fun HttpResponse.logString(body: String): String =
    "[RESPONSE]:${status.value}:${call.request.method.value}:${call.request.url} body:$body"

public suspend fun HttpResponse.errorString(): String {
    val message = String(this.readBytes())
    return logString(message)
}

public fun Request<*, *>.logString(body: String): String {
    val method = route.method.value
    val path = route.path
    val params = routeParams.entries
        .joinToString(",", "[", "]") { (key, value) -> "$key=$value" }

    return "[REQUEST]:$method:$path params:$params body:$body"
}
