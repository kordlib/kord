package dev.kord.rest.ratelimit

import dev.kord.rest.request.Request
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A rate limiter that follows [Discord's rate limits](https://discord.com/developers/docs/topics/rate-limits) for
 * the REST api.
 */
public interface RequestRateLimiter {

    /**
     * Awaits all active rate limits for the [request], returning a [RequestToken] used to process the result of the
     * [request].
     */
    public suspend fun await(request: Request<*, *>): RequestToken

}

/**
 * [Awaits][RequestRateLimiter.await] the rate limits for the [request] and then runs [consumer].
 * Throws an [IllegalStateException] if the supplied [RequestToken] was not completed.
 */
public suspend inline fun <T> RequestRateLimiter.consume(
    request: Request<*, *>,
    consumer: (token: RequestToken) -> T
): T {
    contract {
        callsInPlace(consumer, InvocationKind.EXACTLY_ONCE)
    }
    val token = await(request)
    try {
        val result = consumer(token)
        check(token.completed) {
            token.complete(RequestResponse.Error)
            "token was not completed"
        }
        return result
    } catch (exception: Throwable) {
        if (!token.completed) token.complete(RequestResponse.Error)
        throw exception
    }

}

public data class RateLimit(val total: Total, val remaining: Remaining) {
    val isExhausted: Boolean get() = remaining.value == 0L

    public companion object
}

@JvmInline
public value class Total(public val value: Long)

@JvmInline
public value class Remaining(public val value: Long)

/**
 * The unique identifier of this bucket.
 */
@JvmInline
public value class BucketKey(public val value: String)

/**
 * The [instant][value] when the current bucket gets reset.
 */
@JvmInline
public value class Reset(public val value: Instant)

public sealed class RequestResponse {
    public abstract val bucketKey: BucketKey?
    public abstract val rateLimit: RateLimit?
    public abstract val reset: Reset?

    /**
     * The request returned a non-rate limit error code.
     */
    public object Error : RequestResponse() {
        override val bucketKey: BucketKey? = null
        override val rateLimit: RateLimit? = null
        override val reset: Reset? = null
    }

    /**
     * The request returned a response without errors.
     */
    public data class Accepted(
        override val bucketKey: BucketKey?,
        override val rateLimit: RateLimit?,
        override val reset: Reset
    ) : RequestResponse()

    /**
     * The request returned a global rate limit error.
     */
    public data class GlobalRateLimit(
        override val bucketKey: BucketKey?,
        override val rateLimit: RateLimit?,
        override val reset: Reset
    ) : RequestResponse()

    /**
     * The request returned a bucket rate limit error.
     */
    public data class BucketRateLimit(
        override val bucketKey: BucketKey,
        override val rateLimit: RateLimit?,
        override val reset: Reset
    ) : RequestResponse()

    /**
     * The request returned a rate limit error without a bucket key present.
     */
    public data class UnknownBucketRateLimit(
        override val rateLimit: RateLimit?,
        override val reset: Reset
    ) : RequestResponse() {
        override val bucketKey: BucketKey? = null
    }

    public companion object

}

/**
 * A completable token linked to a [Request].
 */
public interface RequestToken {

    /**
     * Whether [complete] has been called.
     */
    public val completed: Boolean

    /**
     * Completes the [Request], updating the rate limit data from the [response].
     */
    public suspend fun complete(response: RequestResponse)
}
