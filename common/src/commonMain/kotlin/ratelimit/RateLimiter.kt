package dev.kord.common.ratelimit

import kotlinx.datetime.Clock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * A rate limiter that limits the number of [consume] invocations that can be made over a certain period.
 */
public interface RateLimiter {

    /**
     * Acquires a permit for a single action. Suspends if necessary until the permit would not exceed
     * the maximum frequency of permits.
     */
    public suspend fun consume()
}

/**
 * Acquires a permit for a single [action]. Suspends if necessary until the permit would not exceed
 * the maximum frequency of permits.
 *
 * @param action The action that correlates to a single permit.
 */
public suspend inline fun <T> RateLimiter.consume(action: () -> T): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    consume()
    return action()
}

/**
 * A [RateLimiter] that restricts the number of [consume] invocations in [intervals][interval] to a given [limit].
 * Exceeding this limit results in suspension until the next interval.
 */
public interface IntervalRateLimiter : RateLimiter {

    /** The maximum number of [consume] invocations allowed for each interval. */
    public val limit: Int

    /** The [Duration] of each interval. */
    public val interval: Duration

    /**
     * Acquires a permit for a single action. Suspends until the next [interval] if [limit] permits have already been
     * acquired in the current interval.
     */
    override suspend fun consume()
}

/**
 * Creates an implementation of [IntervalRateLimiter] that uses a [TimeSource] for measuring intervals.
 *
 * @param limit The maximum number of [consume] invocations allowed for each interval.
 * @param interval The [Duration] of each interval.
 * @param timeSource The [TimeSource] for measuring intervals, [TimeSource.Monotonic] by default.
 */
public fun IntervalRateLimiter(
    limit: Int,
    interval: Duration,
    timeSource: TimeSource = TimeSource.Monotonic,
): IntervalRateLimiter = TimeSourceIntervalRateLimiter(limit, interval, timeSource)

/**
 * Creates an implementation of [IntervalRateLimiter] that uses a [Clock] for measuring intervals.
 *
 * @param limit The maximum number of [consume] invocations allowed for each interval.
 * @param interval The [Duration] of each interval.
 * @param clock The [Clock] for measuring intervals.
 */
public fun IntervalRateLimiter(limit: Int, interval: Duration, clock: Clock): IntervalRateLimiter =
    ClockIntervalRateLimiter(limit, interval, clock)
