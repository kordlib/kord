package dev.kord.common.ratelimit

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * An implementation of [IntervalRateLimiter] that uses a [Clock] for measuring intervals.
 *
 * @param limit The maximum number of [consume] invocations allowed for each interval.
 * @param interval The [Duration] of each interval.
 * @param clock The [Clock] for measuring intervals, [Clock.System] by default.
 */
public class ClockIntervalRateLimiter(
    limit: Int,
    interval: Duration,
    private val clock: Clock = Clock.System,
) : IntervalRateLimiter(limit, interval) {

    private var intervalEnd = Instant.DISTANT_PAST

    private fun enterNextInterval(now: Instant) {
        // limit - 1, this is already the first 'consume' call in the next interval
        remaining = limit - 1
        intervalEnd = now + interval
    }

    private suspend fun delayUntilNextInterval(now: Instant) {
        val duration = intervalEnd - now
        delay(duration)
    }

    override suspend fun consumeUnderLock() {
        val now = clock.now()
        when {
            now >= intervalEnd -> enterNextInterval(now)
            limitIsExceeded -> {
                delayUntilNextInterval(now)
                enterNextInterval(now = clock.now())
            }
            else -> remaining -= 1
        }
    }
}
