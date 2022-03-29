package ratelimit

import dev.kord.common.ratelimit.ClockIntervalRateLimiter
import dev.kord.common.ratelimit.IntervalRateLimiter
import fixed
import kotlinx.datetime.Clock
import kotlin.time.Duration

class ClockIntervalRateLimiterTest : AbstractIntervalRateLimiterTest() {

    private val clock = Clock.fixed(instant = Clock.System.now())

    override fun newRateLimiter(limit: Int, interval: Duration): IntervalRateLimiter =
        ClockIntervalRateLimiter(limit, interval, clock)
}
