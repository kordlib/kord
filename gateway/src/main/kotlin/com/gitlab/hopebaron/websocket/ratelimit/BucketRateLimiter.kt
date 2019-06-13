package com.gitlab.hopebaron.websocket.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant

/**
 * A rate limiter that supplies a given [capacity] of permits at for each [interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillInterval The interval between permit refills.
 */
@Suppress("FunctionName")
fun BucketRateLimiter(capacity: Int, refillInterval: Duration) = BucketRateLimiter(capacity, refillInterval.toMillis())

/**
 * A rate limiter that supplies a given [capacity] of permits for each [interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillIntervalMillis The interval between permit refills in milliseconds.
 */
class BucketRateLimiter(private val capacity: Int, private val refillIntervalMillis: Long) : RateLimiter {

    private val mutex = Mutex()

    private var count = 0
    private var nextInterval = 0L

    init {
        require(capacity > 0) { "capacity must be a positive number" }
        require(refillIntervalMillis > 0) { "refill interval must be a positive number" }
    }

    private val isNextInterval get() = nextInterval <= nowMillis()

    private val isAtCapacity get() = count == capacity

    private fun resetState() {
        count = 0
        nextInterval = nowMillis() + refillIntervalMillis
    }

    private suspend fun delayUntilNextInterval() {
        val delay = nextInterval - nowMillis()
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


private fun nowMillis(): Long {
    return Instant.now().toEpochMilli()
}