package dev.kord.rest.ratelimit

import dev.kord.common.concurrentHashMap
import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestIdentifier
import dev.kord.rest.request.identifier
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import mu.KLogger
import kotlin.time.Duration.Companion.minutes

public abstract class AbstractRateLimiter internal constructor(public val clock: Clock) : RequestRateLimiter {
    internal abstract val logger: KLogger

    private val autoBanRateLimiter = IntervalRateLimiter(limit = 25000, interval = 10.minutes)
    private val globalSuspensionPoint = atomic(clock.now())
    internal val buckets = concurrentHashMap<BucketKey, Bucket>()
    private val routeBuckets = concurrentHashMap<RequestIdentifier, MutableSet<BucketKey>>()

    internal val BucketKey.bucket get() = buckets.getOrPut(this) { Bucket(this) }
    private val Request<*, *>.buckets get() = routeBuckets[identifier].orEmpty().map { it.bucket }
    internal fun RequestIdentifier.addBucket(id: BucketKey) = routeBuckets.getOrPut(this) { mutableSetOf() }.add(id)

    private suspend fun Instant.await() {
        val duration = this - clock.now()
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
        private val rateLimiter: AbstractRateLimiter,
        private val identity: RequestIdentifier,
        private val requestBuckets: List<Bucket>
    ) : RequestToken {
        private val completableDeferred = CompletableDeferred<Unit>()

        override val completed: Boolean
            get() = completableDeferred.isCompleted

        override suspend fun complete(response: RequestResponse) {
            with(rateLimiter) {
                val key = response.bucketKey
                if (key != null) {
                    if (identity.addBucket(key)) {

                        logger.trace { "[DISCOVERED]:[BUCKET]:Bucket discovered for ${key.bucket.id.value}" }
                        buckets[key] = key.bucket
                    }
                }

                when (response) {
                    is RequestResponse.GlobalRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[GLOBAL]:exhausted until ${response.reset.value}" }
                        globalSuspensionPoint.value = response.reset.value
                    }
                    is RequestResponse.BucketRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[BUCKET]:Bucket ${response.bucketKey.value} was exhausted until ${response.reset.value}" }
                        response.bucketKey.bucket.updateReset(response.reset)
                    }
                    is RequestResponse.Accepted, RequestResponse.Error -> {}
                }

                completableDeferred.complete(Unit)
                requestBuckets.forEach { it.unlock() }
            }
        }
    }

    internal inner class Bucket(val id: BucketKey) {
        private val reset = atomic(clock.now())
        private val mutex = Mutex()

        suspend fun awaitAndLock() {
            mutex.lock()
            logger.trace { "[BUCKET]:Bucket ${id.value} waiting until ${reset.value}" }
            reset.value.await()
        }

        fun updateReset(newValue: Reset) {
            reset.value = newValue.value
        }

        fun unlock() = mutex.unlock()

    }

}
