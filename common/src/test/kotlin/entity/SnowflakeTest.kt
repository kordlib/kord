package entity

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.MILLISECOND
import kotlinx.datetime.Instant
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.*

class SnowflakeTest {

    @Test
    fun `Snowflake with value 0 has timeStamp equal to discordEpochStart`() {
        val snowflake = Snowflake(0)
        assertEquals(Snowflake.discordEpochStart, snowflake.timeStamp)
    }

    @Test
    fun `Snowflake with value -1 has timeStamp equal to endOfTime`() {
        val snowflake = Snowflake(-1)
        assertEquals(Snowflake.endOfTime, snowflake.timeStamp)
    }

    @Test
    fun `Snowflake created from instant far in the past has timeStamp equal to the timeStamp of Snowflake min`() {
        val snowflake = Snowflake(Instant.DISTANT_PAST)
        assertEquals(Snowflake.min.timeStamp, snowflake.timeStamp)
    }

    @Test
    fun `Snowflake created from instant far in the future has timeStamp equal to the timeStamp of Snowflake max`() {
        val snowflake = Snowflake(Instant.DISTANT_FUTURE)
        assertEquals(Snowflake.max.timeStamp, snowflake.timeStamp)
    }

    @Test
    fun `Snowflake's timeStamp calculates an Instant close to the Instant the Snowflake was created from`() {
        val instant = Clock.System.now()
        val snowflake = Snowflake(instant)

        // snowflake timestamps have a millisecond accuracy -> allow +/-1 millisecond from original instant
        val validTimeRange = instant.minus(1, MILLISECOND)..instant.plus(1, MILLISECOND)

        assertContains(validTimeRange, snowflake.timeStamp)
    }

    @Test
    fun `min Snowflake's timeMark has passed`() {
        assertTrue(Snowflake.min.timeMark.hasPassedNow())
    }

    @Test
    fun `max Snowflake's timeMark has not passed`() {
        assertFalse(Snowflake.max.timeMark.hasPassedNow())
    }
}
