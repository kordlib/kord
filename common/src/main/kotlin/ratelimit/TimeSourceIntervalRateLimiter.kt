package dev.kord.common.ratelimit

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal class TimeSourceIntervalRateLimiter(
    limit: Int,
    interval: Duration,
    private val timeSource: TimeSource,
) : AbstractIntervalRateLimiter(limit, interval) {

    private var intervalStart = DISTANT_PAST_MARK

    private fun enterNextInterval() {
        // limit - 1, this is already the first 'consume' call in the next interval
        remainingConsumes = limit - 1
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
            else -> remainingConsumes -= 1
        }
    }


    private companion object {
        private val DISTANT_PAST_MARK: TimeMark = object : TimeMark {
            override fun elapsedNow() = Duration.INFINITE
        }
    }
}
