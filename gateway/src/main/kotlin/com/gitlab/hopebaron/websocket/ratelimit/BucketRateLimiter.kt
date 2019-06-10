package com.gitlab.hopebaron.websocket.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant

/**
 * A rate limiter that supplies a given [capacity] of permits at for each[interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillInterval The interval between permit refills.
 */
@Suppress("FunctionName")
fun BucketRateLimiter(capacity: Int, refillInterval: Duration) = BucketRateLimiter(capacity, refillInterval.toMillis())

/**
 * A rate limiter that supplies a given [capacity] of permits at for each[interval](refillIntervalMillis).
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
        require(capacity > 0) { "capacity must be positive number" }
        require(refillIntervalMillis > 0) { "refill interval must be positive number" }
    }

    override suspend fun consume() {
        mutex.withLock {
            val now = nowMillis()

            if (nextInterval <= now) {
                count = 0
                nextInterval = now + refillIntervalMillis
            }

            count += 1

            if (count >= capacity) {
                val delay = nextInterval - now
                kotlinx.coroutines.delay(delay)
                count = 1
            }
        }
    }
}


private fun nowMillis(): Long {
    return Instant.now().toEpochMilli()
}