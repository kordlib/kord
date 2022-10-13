package dev.kord.rest.ratelimit

import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestIdentifier
import dev.kord.rest.request.identifier
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import mu.KLogger
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes


public abstract class AbstractRateLimiter internal constructor(public val clock: Clock) : RequestRateLimiter {
    internal abstract val logger: KLogger

    // https://discord.com/developers/docs/topics/rate-limits#invalid-request-limit-aka-cloudflare-bans
    internal val autoBanRateLimiter = IntervalRateLimiter(limit = 10_000, interval = 10.minutes)
    internal val globalSuspensionPoint = atomic(Reset(clock.now()))
    internal val routeBuckets = ConcurrentHashMap<RequestIdentifier, ConcurrentHashMap<BucketKey, Bucket>>()
    internal val Request<*, *>.buckets get() = routeBuckets[identifier].orEmpty().values.toList()
    // Fallback bucket key if the request doesn't have any bucket ID
    internal val missingBucket = BucketKey("missing")

    internal fun createBucket(identity: RequestIdentifier, response: RequestResponse): Bucket? {
        val key = response.bucketKey ?: missingBucket
        
        val bucket = routeBuckets
            .getOrPut(identity) { ConcurrentHashMap() }
            .getOrPut(key) {
                logger.trace { "[DISCOVERED]:[BUCKET]:Bucket discovered for ${key.value} (identity $identity)" }
                Bucket(identity, key)
            }

        bucket.updateRateLimit(response.rateLimit, response.reset)
        return bucket
    }

    internal suspend fun Reset.await() {
        val duration = value - clock.now()
        if (duration.isNegative()) return
        delay(duration)
    }

    override suspend fun await(request: Request<*, *>): RequestToken {
        globalSuspensionPoint.value.await()

        val buckets = request.buckets
        buckets.forEach { it.awaitAndLock() }

        autoBanRateLimiter.consume()
        return newToken(request, buckets)
    }

    internal abstract fun newToken(request: Request<*, *>, buckets: List<Bucket>): RequestToken

    internal abstract class AbstractRequestToken(
        val rateLimiter: AbstractRateLimiter,
        val identity: RequestIdentifier,
        val requestBuckets: List<Bucket>
    ) : RequestToken {
        private val completableDeferred = CompletableDeferred<Unit>()

        override val completed: Boolean
            get() = completableDeferred.isCompleted

        override suspend fun complete(response: RequestResponse) {
            with(rateLimiter) {
                rateLimiter.createBucket(identity, response)

                when (response) {
                    is RequestResponse.GlobalRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[GLOBAL]:exhausted until ${response.reset.value}" }
                        globalSuspensionPoint.update { response.reset }
                    }
                    is RequestResponse.BucketRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[BUCKET]:Bucket ${response.bucketKey.value} (identity $identity) was exhausted until ${response.reset.value}" }
                    }
                    is RequestResponse.UnknownBucketRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[BUCKET]:Identity $identity was exhausted until ${response.reset.value}" }
                    }
                    else -> {}
                }

                completableDeferred.complete(Unit)
                requestBuckets.forEach { it.unlock() }
            }
        }
    }

    internal inner class Bucket(val identity: RequestIdentifier, val id: BucketKey) {
        val rateLimitWithReset = atomic<RateLimitWithReset?>(null)
        val mutex = Mutex()

        suspend fun awaitAndLock() {
            mutex.lock()
            val rateLimitWithReset = rateLimitWithReset.value
            val rateLimit = rateLimitWithReset?.rateLimit
            val reset = rateLimitWithReset?.reset

            // Is the rate limit null (can be null if the response doesn't have a key, example: emojis) or are we exausted?
            if (rateLimit == null || rateLimit.isExhausted) {
                // Yes, we are, so we need to wait for the rate limit reset!
                if (reset != null) {
                    logger.trace { "[BUCKET]:Bucket ${id.value} (identity $identity) waiting until ${reset.value}" }
                    reset.await()
                } else {
                    logger.warn { "[BUCKET]:Bucket ${id.value} (identity $identity) is exausted, however we don't have any information about the reset timer" }
                }
            }
        }

        fun updateRateLimit(newRateLimit: RateLimit?, newReset: Reset?) {
            rateLimitWithReset.update { RateLimitWithReset(newRateLimit, newReset) }
        }

        fun unlock() = mutex.unlock()

    }

    internal data class RateLimitWithReset(val rateLimit: RateLimit?, val reset: Reset?)
}
