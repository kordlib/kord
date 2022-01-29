package dev.kord.common.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration


/**
 * A rate limiter that supplies a given [capacity] of permits for each [interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillInterval The duration between permit refills.
 */
public class BucketRateLimiter(
    private val capacity: Int,
    private val refillInterval: Duration,
    private val clock: Clock = Clock.System
) : RateLimiter {

    private val mutex = Mutex()

    private var count = 0
    private var nextInterval = Instant.fromEpochMilliseconds(0)

    init {
        require(capacity > 0) { "capacity must be a positive number" }
        require(refillInterval.isPositive()) { "refill interval must be positive" }
    }

    private val isNextInterval get() = nextInterval <= clock.now()

    private val isAtCapacity get() = count == capacity

    private fun resetState() {
        count = 0
        nextInterval = clock.now() + refillInterval
    }

    private suspend fun delayUntilNextInterval() {
        val delay = nextInterval - clock.now()
        kotlinx.coroutines.delay(delay)
    }

    override suspend fun consume(): Unit = mutex.withLock {
        if (isNextInterval) resetState()

        if (isAtCapacity) {
            delayUntilNextInterval()
            resetState()
        }

        count += 1
    }
}
