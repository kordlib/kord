package ratelimit

import dev.kord.common.ratelimit.ClockIntervalRateLimiter
import fixed
import kotlinx.datetime.Clock
import kotlin.time.Duration

class ClockIntervalRateLimiterTest : IntervalRateLimiterTest() {

    private val clock = Clock.fixed(instant = Clock.System.now())

    override fun newRateLimiter(limit: Int, interval: Duration) =
        ClockIntervalRateLimiter(limit, interval, clock)
}
