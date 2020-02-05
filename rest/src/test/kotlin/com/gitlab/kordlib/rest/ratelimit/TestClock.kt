package com.gitlab.kordlib.rest.ratelimit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@ExperimentalCoroutinesApi
class TestClock(val instant: Instant, val scope: TestCoroutineScope, val zoneId: ZoneId) : Clock() {
    override fun getZone(): ZoneId = zoneId
    override fun instant(): Instant = instant.plusMillis(scope.currentTime)
    override fun withZone(zone: ZoneId): Clock = TestClock(instant, scope, zone)
}
