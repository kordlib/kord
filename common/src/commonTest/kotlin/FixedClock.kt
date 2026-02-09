package dev.kord.common

import kotlin.time.Clock
import kotlin.time.Instant

fun Clock.Companion.fixed(instant: Instant): Clock = FixedClock(instant)

private class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}
