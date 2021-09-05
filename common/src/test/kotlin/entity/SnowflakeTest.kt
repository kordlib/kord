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
    fun `min Snowflake's timestamp is equal to discordEpoch`() {
        assertEquals(Snowflake.discordEpoch, Snowflake.min.timestamp)
    }

    @Test
    fun `max Snowflake's timestamp is equal to endOfTime`() {
        assertEquals(Snowflake.endOfTime, Snowflake.max.timestamp)
    }

    @Test
    fun `Snowflake created from ULong MIN_VALUE has timestamp equal to discordEpoch`() {
        val snowflake = Snowflake(ULong.MIN_VALUE)
        assertEquals(Snowflake.discordEpoch, snowflake.timestamp)
    }

    @Test
    fun `Snowflake created from ULong MAX_VALUE has timestamp equal to endOfTime`() {
        val snowflake = Snowflake(ULong.MAX_VALUE)
        assertEquals(Snowflake.endOfTime, snowflake.timestamp)
    }

    @Test
    fun `Snowflake created from Long MIN_VALUE has timestamp equal to discordEpoch`() {
        val snowflake = Snowflake(Long.MIN_VALUE)
        assertEquals(Snowflake.discordEpoch, snowflake.timestamp)
    }

    @Test
    fun `Snowflake created from instant far in the past has timestamp equal to discordEpoch`() {
        val snowflake = Snowflake(Instant.DISTANT_PAST)
        assertEquals(Snowflake.discordEpoch, snowflake.timestamp)
    }

    @Test
    fun `Snowflake created from instant far in the future has timestamp equal to endOfTime`() {
        val snowflake = Snowflake(Instant.DISTANT_FUTURE)
        assertEquals(Snowflake.endOfTime, snowflake.timestamp)
    }

    @Test
    fun `Snowflake's timestamp calculates an Instant close to the Instant the Snowflake was created from`() {
        val instant = Clock.System.now()
        val snowflake = Snowflake(instant)

        // snowflake timestamps have a millisecond accuracy -> allow +/-1 millisecond from original instant
        val validTimeRange = instant.minus(1, MILLISECOND)..instant.plus(1, MILLISECOND)

        assertContains(validTimeRange, snowflake.timestamp)
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
