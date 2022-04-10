package ratelimit

import dev.kord.common.ratelimit.IntervalRateLimiter
import dev.kord.common.ratelimit.TimeSourceIntervalRateLimiter
import kotlin.time.Duration
import kotlin.time.TestTimeSource

class TimeSourceIntervalRateLimiterTest : AbstractIntervalRateLimiterTest() {

    private val timeSource = TestTimeSource()

    override fun newRateLimiter(limit: Int, interval: Duration): IntervalRateLimiter =
        TimeSourceIntervalRateLimiter(limit, interval, timeSource)
}
