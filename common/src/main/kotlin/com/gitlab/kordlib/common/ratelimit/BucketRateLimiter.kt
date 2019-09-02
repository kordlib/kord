package com.gitlab.kordlib.common.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Clock
import java.time.Duration as JavaDuration
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.toKotlinDuration

/**
 * A rate limiter that supplies a given [capacity] of permits at for each [interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillInterval The interval between permit refills.
 */
@Suppress("FunctionName")
@Deprecated("use Kotlin's duration instead", ReplaceWith("refillInterval.toKotlinDuration()", "kotlin.time"))
fun BucketRateLimiter(capacity: Int, refillInterval: JavaDuration) = BucketRateLimiter(capacity, refillInterval.toKotlinDuration())


/**
 * A rate limiter that supplies a given [capacity] of permits for each [interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillInterval The duration between permit refills.
 */
class BucketRateLimiter(private val capacity: Int, private val refillInterval: Duration, private val clock: Clock = Clock.systemUTC()) : RateLimiter {

    @Deprecated("use Duration instead", ReplaceWith("refillInterval.milliseconds", "kotlin.time.toKotlinDuration"))
    constructor(capacity: Int, refillInterval: Long): this(capacity, refillInterval.milliseconds)

    private val mutex = Mutex()

    private var count = 0
    private var nextInterval = 0L

    init {
        require(capacity > 0) { "capacity must be a positive number" }
        require(refillInterval.isPositive()) { "refill interval must be positive" }
    }

    private val isNextInterval get() = nextInterval <= clock.millis()

    private val isAtCapacity get() = count == capacity

    private fun resetState() {
        count = 0
        nextInterval = clock.millis() + refillInterval.inMilliseconds.toLong()
    }

    private suspend fun delayUntilNextInterval() {
        val delay = nextInterval - clock.millis()
        kotlinx.coroutines.delay(delay)
    }

    override suspend fun consume() = mutex.withLock {
        if (isNextInterval) resetState()

        if (isAtCapacity) {
            delayUntilNextInterval()
            resetState()
        }

        count += 1
    }
}
