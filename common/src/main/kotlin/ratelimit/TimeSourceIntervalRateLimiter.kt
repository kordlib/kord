package dev.kord.common.ratelimit

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * An implementation of [IntervalRateLimiter] that uses a [TimeSource] for measuring intervals.
 *
 * @param limit The maximum number of [consume] invocations allowed for each interval.
 * @param interval The [Duration] of each interval.
 * @param timeSource The [TimeSource] for measuring intervals, [TimeSource.Monotonic] by default.
 */
public class TimeSourceIntervalRateLimiter(
    limit: Int,
    interval: Duration,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) : IntervalRateLimiter(limit, interval) {

    private var intervalStart = DISTANT_PAST_MARK

    private fun enterNextInterval() {
        // limit - 1, this is already the first 'consume' call in the next interval
        remaining = limit - 1
        intervalStart = timeSource.markNow()
    }

    private suspend fun delayUntilNextInterval(elapsed: Duration) {
        val duration = interval - elapsed
        delay(duration)
    }

    override suspend fun consumeUnderLock() {
        val elapsed = intervalStart.elapsedNow()
        when {
            elapsed >= interval -> enterNextInterval()
            limitIsExceeded -> {
                delayUntilNextInterval(elapsed)
                enterNextInterval()
            }
            else -> remaining -= 1
        }
    }


    private companion object {
        private val DISTANT_PAST_MARK: TimeMark = object : TimeMark() {
            override fun elapsedNow() = Duration.INFINITE
        }
    }
}
