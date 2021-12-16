package dev.kord.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

@ExperimentalCoroutinesApi
class TestClock(val instant: Instant, val scope: TestScope) : Clock {
    override fun now(): Instant = instant + Duration.milliseconds(scope.currentTime)
}
