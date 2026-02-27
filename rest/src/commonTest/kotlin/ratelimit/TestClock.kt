package dev.kord.rest.ratelimit

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class TestClock(private val instant: Instant, private val scope: TestScope) : Clock {
    override fun now(): Instant = instant + scope.currentTime.milliseconds
}
