package entity

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

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
        val delta = 1.milliseconds - 1.nanoseconds
        val validTimeRange = (instant - delta)..(instant + delta)

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

    @Test
    fun `Snowflake can be destructured`() {
        val snowflake = Snowflake(0b110010110111_10111_01101_101100111101_u)
        val (timestamp, worker, process, increment) = snowflake

        assertEquals(snowflake.timestamp, timestamp)
        assertEquals(snowflake.workerId, worker)
        assertEquals(snowflake.processId, process)
        assertEquals(snowflake.increment, increment)

        assertEquals(Instant.fromEpochMilliseconds(0b110010110111 + 1420070400000), timestamp)
        assertEquals(0b10111u, worker)
        assertEquals(0b01101u, process)
        assertEquals(0b101100111101u, increment)
    }
}
