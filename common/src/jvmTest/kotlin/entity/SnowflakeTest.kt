package entity

import dev.kord.common.entity.Snowflake
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SnowflakeTestJvm {
    @Test
    fun `Snowflake's natural order works with SortedSets`() {
        val a = Snowflake(0b0_00000_00000_000000000000_u)
        val b = Snowflake(0b0_00000_00000_000000000001_u)
        val c = Snowflake(0b1_00000_00000_000000000000_u)
        assertEquals(2, sortedSetOf(a, b).size)
        assertEquals(2, sortedSetOf(a, c).size)
    }
}
