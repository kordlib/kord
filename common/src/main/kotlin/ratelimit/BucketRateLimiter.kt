package dev.kord.common.ratelimit

import kotlinx.datetime.Clock
import kotlin.time.Duration


/**
 * A rate limiter that supplies a given [capacity] of permits for each [interval](refillIntervalMillis).
 * Exceeding this [capacity] will result in a suspend until the next [interval](refillIntervalMillis).
 *
 * @param capacity The maximum amount of permits that are given for each permit.
 * @param refillInterval The duration between permit refills.
 */
@Deprecated(
    "Replaced by 'IntervalRateLimiter' that uses 'TimeSource' instead of 'Clock' by default.",
    ReplaceWith(
        "IntervalRateLimiter(limit = capacity, interval = refillInterval)",
        "dev.kord.common.ratelimit.IntervalRateLimiter",
    ),
)
public class BucketRateLimiter(
    capacity: Int,
    refillInterval: Duration,
    clock: Clock = Clock.System,
) : RateLimiter by ClockIntervalRateLimiter(limit = capacity, interval = refillInterval, clock)
