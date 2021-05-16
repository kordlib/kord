import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun Clock.Companion.fixed(instant: Instant): Clock = FixedClock(instant)

private class FixedClock(private val instant: Instant): Clock {
    override fun now(): Instant = instant
}
