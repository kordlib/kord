package dev.kord.common.ratelimit

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

internal class ClockIntervalRateLimiter(
    limit: Int,
    interval: Duration,
    private val clock: Clock,
) : AbstractIntervalRateLimiter(limit, interval) {

    private var intervalEnd = Instant.DISTANT_PAST

    private fun enterNextInterval(now: Instant) {
        // limit - 1, this is already the first 'consume' call in the next interval
        remainingConsumes = limit - 1
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
            else -> remainingConsumes -= 1
        }
    }
}
