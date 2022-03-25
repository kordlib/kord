package dev.kord.common.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

/**
 * A [RateLimiter] that restricts the number of [consume] invocations in [intervals][interval] to a given [limit].
 * Exceeding this limit results in suspension until the next interval.
 *
 * @param limit The maximum number of [consume] invocations allowed for each interval.
 * @param interval The [Duration] of each interval.
 */
public abstract class IntervalRateLimiter(
    protected val limit: Int,
    protected val interval: Duration,
) : RateLimiter {

    /** Remaining number of [consume] invocations allowed in the current interval. */
    protected var remaining: Int = limit

    /** Whether no more [consume] invocations are allowed in the current interval. */
    protected val limitIsExceeded: Boolean get() = remaining == 0

    init {
        require(limit > 0) { "limit must be positive but was $limit" }
        require(interval.isPositive()) { "interval must be positive but was $interval" }
        require(interval.isFinite()) { "interval must be finite but was $interval" }
    }

    private val mutex: Mutex = Mutex()

    /**
     * Acquires a permit for a single action. Suspends until the next [interval] if [limit] permits have already been
     * acquired in the current interval.
     */
    final override suspend fun consume() {
        mutex.withLock { consumeUnderLock() }
    }

    /** Version of [consume] that is called under a lock, e.g. editing vars is thread-safe in this method. */
    protected abstract suspend fun consumeUnderLock()
}
