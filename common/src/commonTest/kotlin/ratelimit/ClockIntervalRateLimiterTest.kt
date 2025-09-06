package dev.kord.common.ratelimit

import dev.kord.common.fixed
import kotlin.time.Clock
import kotlin.time.Duration

class ClockIntervalRateLimiterTest : AbstractIntervalRateLimiterTest() {

    private val clock = Clock.fixed(instant = Clock.System.now())

    override fun newRateLimiter(limit: Int, interval: Duration): IntervalRateLimiter =
        ClockIntervalRateLimiter(limit, interval, clock)
}
