import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet
import kotlin.test.*

class BitSetTests {
    @Test
    fun `a contains b and c`() {
        val a = DiscordBitSet(0b111)
        val b = DiscordBitSet(0b101)
        val c = DiscordBitSet(0b101, 0)
        assertTrue(b in a)
        assertTrue(c in a)
    }

    @Test
    fun `a and b are equal and have the same hashCode`() {
        val a = DiscordBitSet(0b111, 0)
        val b = DiscordBitSet(0b111)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `a does not equal b`() {
        val a = DiscordBitSet(0b111, 0)
        val b = DiscordBitSet(0b111, 0b1)
        assertNotEquals(a, b)
    }

    @Test
    fun `get bits`() {
        val a = DiscordBitSet(0b101, 0)
        assertTrue(a[0])
        assertFalse(a[1])
        assertTrue(a[2])
        for (i in 3..64) assertFalse(a[i])

        val b = DiscordBitSet(1L shl 63)
        for (i in 0..62) assertFalse(b[i])
        assertTrue(b[63])
    }

    @Test
    fun `set bits`() {
        val a = EmptyBitSet()
        for (i in 0..64) a[i] = true
        assertEquals(DiscordBitSet(ULong.MAX_VALUE.toLong(), 1), a)

        val b = EmptyBitSet()
        b[1] = true
        b[2] = true
        b[5] = true
        assertEquals(DiscordBitSet(0b100110), b)
        b[2] = false
        assertEquals(DiscordBitSet(0b100010), b)
    }

    @Test
    fun `get a bit out of range`() {
        val a = DiscordBitSet(0b101, 0)
        assert(!a[10000])
    }

    @Test
    fun `add and remove a bit`() {
        val a = DiscordBitSet(0b101, 0)
        a.add(DiscordBitSet(0b111))
        assert(a.value == 0b111.toString())
        a.remove(DiscordBitSet(0b001))
        assert(a.value == 0b110.toString())
    }

    @Test
    fun `remove a bit`() {
        val a = DiscordBitSet(0b101, 0)
        a.remove(DiscordBitSet(0b111))
        assert(a.value == "0")
    }

    @Test
    fun `binary works`() {
        assertEquals("0", DiscordBitSet().binary)
        assertEquals("0", DiscordBitSet(0).binary)
        assertEquals("10011", DiscordBitSet(0b10011).binary)
        assertEquals(
            "110" +
                "0000000000000000000000000000000000000000000000000000000000111001" +
                "0000000000000000000000000000000000000000000000000000000000001011",
            DiscordBitSet(0b1011, 0b111001, 0b110).binary,
        )
    }
}
