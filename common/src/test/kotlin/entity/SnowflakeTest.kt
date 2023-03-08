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

    @Test
    fun `Snowflakes are compared correctly`() {
        //                      timestamp  worker  process  increment
        //                        vvv        vvv    vvv         vvv
        val a = Snowflake(0b0000000000000001_00010_00100_000000001000_u)
        val b = Snowflake(0b0000000000000010_00001_00010_000000000100_u)
        val c = Snowflake(0b0000000000000011_00000_00001_000000000010_u)
        val d = Snowflake(0b0000000000000011_00001_00000_000000000001_u)
        val e = Snowflake(0b0000000000000011_00001_00001_000000000000_u)
        val f = Snowflake(0b0000000000000011_00001_00001_000000000001_u)
        assertTrue(a < b)
        assertTrue(b < c)
        assertTrue(a < c)
        assertTrue(c < d)
        assertTrue(d < e)
        assertTrue(e < f)
        assertTrue(a < f)
        assertTrue(c < f)
        with(Snowflake.TimestampComparator) {
            assertTrue(compare(a, b) < 0)
            assertTrue(compare(b, c) < 0)
            assertTrue(compare(a, c) < 0)
            assertEquals(0, compare(c, d))
            assertEquals(0, compare(d, e))
            assertEquals(0, compare(e, f))
            assertTrue(compare(a, f) < 0)
            assertEquals(0, compare(c, f))
        }
    }

    @Test
    fun `Snowflake's natural order works with SortedSets`() {
        val a = Snowflake(0b0_00000_00000_000000000000_u)
        val b = Snowflake(0b0_00000_00000_000000000001_u)
        val c = Snowflake(0b1_00000_00000_000000000000_u)
        assertEquals(2, sortedSetOf(a, b).size)
        assertEquals(2, sortedSetOf(a, c).size)
    }
}
