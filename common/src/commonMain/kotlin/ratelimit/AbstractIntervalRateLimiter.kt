package dev.kord.common.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

/** Abstract base class for implementing [IntervalRateLimiter]. */
public abstract class AbstractIntervalRateLimiter(
    final override val limit: Int,
    final override val interval: Duration,
) : IntervalRateLimiter {

    /** Remaining number of [consume] invocations allowed in the current interval. */
    protected var remainingConsumes: Int = limit

    /** Whether no more [consume] invocations are allowed in the current interval. */
    protected val limitIsExceeded: Boolean get() = remainingConsumes == 0

    init {
        require(limit > 0) { "limit must be positive but was $limit" }
        require(interval.isPositive()) { "interval must be positive but was $interval" }
        require(interval.isFinite()) { "interval must be finite but was $interval" }
    }

    private val mutex = Mutex()

    final override suspend fun consume() {
        mutex.withLock { consumeUnderLock() }
    }

    /** Variant of [consume] that is called under a lock, e.g. editing vars is thread-safe in this method. */
    protected abstract suspend fun consumeUnderLock()
}
