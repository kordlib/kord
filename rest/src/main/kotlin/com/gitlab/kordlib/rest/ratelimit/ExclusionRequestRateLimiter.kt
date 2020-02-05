package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestIdentifier
import com.gitlab.kordlib.rest.request.identifier
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import mu.KotlinLogging
import java.time.Clock
import java.time.Duration
import java.time.Instant
import kotlin.time.minutes

internal inline class ResetPoint(val instant: Instant) {
    suspend fun await(clock: Clock) {
        val duration = Duration.between(clock.instant(), instant)
        if (duration.isNegative) return
        delay(duration.toMillis())
    }
}

internal fun Reset.toResetPoint(): ResetPoint = ResetPoint(value)

private val logger = KotlinLogging.logger("[R]:[ExclusionRequestRateLimiter]")

/**
 * A [RequestRateLimiter] that handles all [requests][Request] in sequential order,
 * minimizing the possibility of rate limits. Requests are handled in call order
 * and will suspend to adhere to global and bucket rate limits.
 *
 * @param clock a [Clock] used for calculating suspension times, present for testing purposes.
 */
class ExclusionRequestRateLimiter(val clock: Clock = Clock.systemUTC()) : RequestRateLimiter {

    internal val mutex = Mutex()
    internal var globalPoint: ResetPoint = ResetPoint(clock.instant())
    internal val buckets: MutableMap<BucketKey, ResetPoint> = mutableMapOf()
    internal val requestBuckets: MutableMap<RequestIdentifier, MutableSet<BucketKey>> = mutableMapOf()
    internal val autoBanRateLimiter = BucketRateLimiter(25000, 10.minutes)

    override suspend fun await(request: Request<*, *>): RequestToken {
        mutex.lock()
        globalPoint.await(clock)
        val bucketKeys = requestBuckets[request.identifier].orEmpty()
        for (bucket in bucketKeys) {
            buckets[bucket]?.await(clock)
            buckets.remove(bucket)
        }

        return ExclusionRequestToken(this, request.identifier)
    }

}

private class ExclusionRequestToken(
        private val rateLimiter: ExclusionRequestRateLimiter,
        private val identity: RequestIdentifier
) : RequestToken {

    val completedAtomic = atomic(false)
    override val completed: Boolean get() = completedAtomic.value

    override suspend fun complete(response: RequestResponse) {
        logger.trace { response.toString() }

        if (response.rateLimit?.isExhausted == true) {
            response.bucketKey?.let { rateLimiter.buckets[it] = response.reset.toResetPoint() }
            logger.trace { "[RATE LIMIT]:[BUCKET]:${response.bucketKey?.value} was exhausted until ${response.reset.value}" }
        }

        if (response.bucketKey != null) {
            val buckets = rateLimiter.requestBuckets.getOrPut(identity, ::mutableSetOf)
            if (response.bucketKey!! !in buckets) {
                logger.trace { "[DISCOVERED]:[BUCKET]:Bucket ${response.bucketKey?.value} discovered for $identity" }
            }
            buckets.add(response.bucketKey!!)
        }

        when (response) {
            is RequestResponse.GlobalRateLimit -> {
                logger.trace { "[RATE LIMIT]:[GLOBAL]:exhausted until ${response.reset.value}" }
                rateLimiter.globalPoint = response.reset.toResetPoint()
                rateLimiter.autoBanRateLimiter.consume()
            }
            is RequestResponse.BucketRateLimit -> {
                logger.trace { "[RATE LIMIT]:[BUCKET]:${response.bucketKey.value} was already exhausted" }
                rateLimiter.buckets[response.bucketKey] = response.reset.toResetPoint()
                rateLimiter.autoBanRateLimiter.consume()
            }
        }

        completedAtomic.compareAndSet(expect = false, update = true)
        rateLimiter.mutex.unlock()
    }

}