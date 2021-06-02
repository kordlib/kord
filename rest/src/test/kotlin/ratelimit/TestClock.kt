package dev.kord.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

@ExperimentalCoroutinesApi
class TestClock(val instant: Instant, val scope: TestCoroutineScope) : Clock {
    override fun now(): Instant = instant + Duration.milliseconds(scope.currentTime)
}
