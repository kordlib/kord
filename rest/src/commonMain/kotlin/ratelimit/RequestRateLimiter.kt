package dev.kord.rest.ratelimit

import dev.kord.rest.request.Request
import kotlinx.datetime.Instant
import kotlin.DeprecationLevel.HIDDEN
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.js.JsName
import kotlin.jvm.JvmInline

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
@Deprecated(
    "Use 'Instant' directly instead.",
    ReplaceWith("Instant", "kotlinx.datetime.Instant"),
    DeprecationLevel.WARNING,
)
@JvmInline
public value class Reset(public val value: Instant)

public sealed class RequestResponse {
    public abstract val bucketKey: BucketKey?
    public abstract val rateLimit: RateLimit?
    public abstract val reset: Instant?

    @Suppress("FunctionName")
    @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
    @JsName("getReset")
    public abstract fun `getReset-8536Nbg`(): Instant?

    /**
     * The request returned a non-rate limit error code.
     */
    public object Error : RequestResponse() {
        override val bucketKey: BucketKey? = null
        override val rateLimit: RateLimit? = null
        override val reset: Instant? = null

        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        override fun `getReset-8536Nbg`(): Instant? = reset
    }

    /**
     * The request returned a response without errors.
     */
    public data class Accepted(
        override val bucketKey: BucketKey?,
        override val rateLimit: RateLimit?,
        override val reset: Instant,
    ) : RequestResponse() {
        @Suppress("FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_component3")
        public fun `component3-Ad4v_Ag`(): Instant = reset

        @Suppress("FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_copy")
        public fun `copy-ociLOyk`(
            bucketKey: String? = this.bucketKey?.value,
            rateLimit: RateLimit? = this.rateLimit,
            reset: Instant = this.reset,
        ): Accepted = Accepted(bucketKey?.let(::BucketKey), rateLimit, reset)

        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        override fun `getReset-8536Nbg`(): Instant = reset

        @Suppress("NON_FINAL_MEMBER_IN_FINAL_CLASS", "FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_getReset")
        public open fun `getReset-Ad4v_Ag`(): Instant = reset
    }

    /**
     * The request returned a global rate limit error.
     */
    public data class GlobalRateLimit(
        override val bucketKey: BucketKey?,
        override val rateLimit: RateLimit?,
        override val reset: Instant,
    ) : RequestResponse() {
        @Suppress("FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_component3")
        public fun `component3-Ad4v_Ag`(): Instant = reset

        @Suppress("FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_copy")
        public fun `copy-ociLOyk`(
            bucketKey: String? = this.bucketKey?.value,
            rateLimit: RateLimit? = this.rateLimit,
            reset: Instant = this.reset,
        ): GlobalRateLimit = GlobalRateLimit(bucketKey?.let(::BucketKey), rateLimit, reset)

        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        override fun `getReset-8536Nbg`(): Instant = reset

        @Suppress("NON_FINAL_MEMBER_IN_FINAL_CLASS", "FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_getReset")
        public open fun `getReset-Ad4v_Ag`(): Instant = reset
    }

    /**
     * The request returned a bucket rate limit error.
     */
    public data class BucketRateLimit(
        override val bucketKey: BucketKey,
        override val rateLimit: RateLimit?,
        override val reset: Instant,
    ) : RequestResponse() {
        @Suppress("FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_component3")
        public fun `component3-Ad4v_Ag`(): Instant = reset

        @Suppress("FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_copy")
        public fun `copy-L_klSFE`(
            bucketKey: String = this.bucketKey.value,
            rateLimit: RateLimit? = this.rateLimit,
            reset: Instant = this.reset,
        ): BucketRateLimit = BucketRateLimit(BucketKey(bucketKey), rateLimit, reset)

        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        override fun `getReset-8536Nbg`(): Instant = reset

        @Suppress("NON_FINAL_MEMBER_IN_FINAL_CLASS", "FunctionName")
        @Deprecated("Binary compatibility, keep for some releases.", level = HIDDEN)
        @JsName("_getReset")
        public open fun `getReset-Ad4v_Ag`(): Instant = reset
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
