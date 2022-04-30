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

    internal val autoBanRateLimiter = IntervalRateLimiter(limit = 25000, interval = 10.minutes)
    internal val globalSuspensionPoint = atomic(Reset(clock.now()))
    internal val buckets = ConcurrentHashMap<BucketKey, Bucket>()
    internal val routeBuckets = ConcurrentHashMap<RequestIdentifier, MutableSet<BucketKey>>()

    internal val BucketKey.bucket get() = buckets.getOrPut(this) { Bucket(this) }
    internal val Request<*, *>.buckets get() = routeBuckets[identifier].orEmpty().map { it.bucket }
    internal fun RequestIdentifier.addBucket(id: BucketKey) = routeBuckets.getOrPut(this) { mutableSetOf() }.add(id)

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
                val key = response.bucketKey
                if (key != null) {
                    if (identity.addBucket(key)) {

                        logger.trace { "[DISCOVERED]:[BUCKET]:Bucket discovered for" }
                        buckets[key] = key.bucket
                    }
                }

                when (response) {
                    is RequestResponse.GlobalRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[GLOBAL]:exhausted until ${response.reset.value}" }
                        globalSuspensionPoint.update { response.reset }
                    }
                    is RequestResponse.BucketRateLimit -> {
                        logger.trace { "[RATE LIMIT]:[BUCKET]:Bucket ${response.bucketKey.value} was exhausted until ${response.reset.value}" }
                        response.bucketKey.bucket.updateReset(response.reset)
                    }
                    else -> {}
                }

                completableDeferred.complete(Unit)
                requestBuckets.forEach { it.unlock() }
            }
        }
    }

    internal inner class Bucket(val id: BucketKey) {
        val reset = atomic(Reset(clock.now()))
        val mutex = Mutex()

        suspend fun awaitAndLock() {
            mutex.lock()
            logger.trace { "[BUCKET]:Bucket ${id.value} waiting until ${reset.value}" }
            reset.value.await()
        }

        fun updateReset(newValue: Reset) {
            reset.update { newValue }
        }

        fun unlock() = mutex.unlock()

    }

}
