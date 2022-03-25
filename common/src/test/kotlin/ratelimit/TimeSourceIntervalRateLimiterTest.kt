package ratelimit

import dev.kord.common.ratelimit.TimeSourceIntervalRateLimiter
import kotlin.time.Duration
import kotlin.time.TestTimeSource

class TimeSourceIntervalRateLimiterTest : IntervalRateLimiterTest() {

    private val timeSource = TestTimeSource()

    override fun newRateLimiter(limit: Int, interval: Duration) =
        TimeSourceIntervalRateLimiter(limit, interval, timeSource)
}
