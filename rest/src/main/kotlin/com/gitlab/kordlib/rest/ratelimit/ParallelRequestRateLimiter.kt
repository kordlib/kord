package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.common.annotation.KordUnsafe
import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestIdentifier
import com.gitlab.kordlib.rest.request.identifier
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import mu.KotlinLogging
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger("[R]:[ParallelRequestRateLimiter]")

/**
 * A [RequestRateLimiter] that tries to handle [requests][Request] in a parallel order.
 * Requests are run sequentially per bucket,
 * allowing requests that do not share a common rate limit to run uninterrupted.
 * Requests that share a common bucket are handled in call order
 * and will suspend to adhere to global and bucket rate limits.
 *
 * Using this [RequestRateLimiter] increases the chance of a exceeding the global rate limit, which in exceedingly
 * unlikely cases might result in your bot's account getting temporarily banned.
 * As such, the [ExclusionRequestRateLimiter] is generally preferred.
 *
 * @param clock a [Clock] used for calculating suspension times, present for testing purposes.
 */
@KordUnsafe
class ParallelRequestRateLimiter(val clock: Clock = Clock.systemUTC()) : RequestRateLimiter {
    internal var globalPoint: ResetPoint = ResetPoint(clock.instant())
    internal val buckets: MutableMap<BucketKey, ResetPoint> = ConcurrentHashMap()
    internal val requestBuckets: MutableMap<RequestIdentifier, MutableSet<BucketKey>> = ConcurrentHashMap()
    internal val locks: MutableMap<BucketKey, Mutex> = ConcurrentHashMap()

    override suspend fun await(request: Request<*, *>): RequestToken {
        globalPoint.await(clock)

        val bucketKeys = requestBuckets[request.identifier].orEmpty()
        val mutexes = bucketKeys.map { locks.getOrPut(it) { Mutex() } }

        mutexes.forEach { it.lock() }

        for (bucket in requestBuckets[request.identifier].orEmpty()) {
            buckets[bucket]?.await(clock)
            buckets.remove(bucket)
        }

        return ParallelRequestToken(this, request.identifier, mutexes)
    }

}

@KordUnsafe
private class ParallelRequestToken(
        val rateLimiter: ParallelRequestRateLimiter,
        val identity: RequestIdentifier,
        val mutexes: List<Mutex>
) : RequestToken {

    val completedAtomic = atomic(false)
    override val completed: Boolean get() = completedAtomic.value

    override suspend fun complete(response: RequestResponse) {
        logger.trace { response.toString() }

        try {
            if (response is RequestResponse.Error) return run {
                completedAtomic.compareAndSet(expect = false, update = true)
                mutexes.forEach { it.unlock() }
            }

            if (response.rateLimit?.isExhausted == true) {
                response.bucketKey?.let { rateLimiter.buckets[it] = response.reset!!.toResetPoint() }
                logger.trace { "[RATE LIMIT]:[BUCKET]:${response.bucketKey?.value} was exhausted until ${response.reset!!.value}" }
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
                }
                is RequestResponse.BucketRateLimit -> {
                    logger.trace { "[RATE LIMIT]:[BUCKET]:${response.bucketKey.value} was already exhausted" }
                    rateLimiter.buckets[response.bucketKey] = response.reset.toResetPoint()
                }
            }
        } finally {
            completedAtomic.compareAndSet(expect = false, update = true)
            mutexes.forEach { it.unlock() }
        }


    }

}
