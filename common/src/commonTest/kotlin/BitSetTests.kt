package dev.kord.common

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.js.JsName
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.test.*

class BitSetTests {
    @Test
    @JsName("test1")
    fun `a contains b and c`() {
        val a = DiscordBitSet(0b111)
        val b = DiscordBitSet(0b101)
        val c = DiscordBitSet(0b101, 0)
        assertTrue(b in a)
        assertTrue(c in a)
    }

    @Test
    @JsName("test2")
    fun `a and b are equal and have the same hashCode`() {
        val a = DiscordBitSet(0b111, 0)
        val b = DiscordBitSet(0b111)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    @JsName("test3")
    fun `a does not equal b`() {
        val a = DiscordBitSet(0b111, 0)
        val b = DiscordBitSet(0b111, 0b1)
        assertNotEquals(a, b)
    }

    @Test
    @JsName("test4")
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
    @JsName("test5")
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
    @JsName("test6")
    fun `get a bit out of range`() {
        val a = DiscordBitSet(0b101, 0)
        assertFalse(a[10000])
    }

    @Test
    @JsName("test7")
    fun `add and remove a bit`() {
        val a = DiscordBitSet(0b101, 0)
        a.add(DiscordBitSet(0b111))
        assertEquals(0b111.toString(), a.value)
        a.remove(DiscordBitSet(0b001))
        assertEquals(0b110.toString(), a.value)
    }

    @Test
    @JsName("test8")
    fun `remove a bit`() {
        val a = DiscordBitSet(0b101, 0)
        a.remove(DiscordBitSet(0b111))
        assertEquals("0", a.value)
    }

    @Test
    @JsName("test9")
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

    @Test
    fun value_works_for_DiscordBitSet_with_empty_data_array() {
        val bits = DiscordBitSet(data = LongArray(size = 0))
        assertEquals("0", bits.value)
    }

    @Test
    fun value_works_for_all_single_bit_Longs() {
        for (shift in 0..<Long.SIZE_BITS) {
            val value = 1L shl shift
            val bits = DiscordBitSet(value)
            assertEquals(value.toULong().toString(), bits.value)
        }
    }

    @Test
    fun value_is_never_negative() {
        for (size in 1..10) {
            val data = LongArray(size)
            data[size - 1] = Random.nextLong(Long.MIN_VALUE..-1)
            val bits = DiscordBitSet(data)
            assertTrue(bits.value.all { it in '0'..'9' })
        }
    }

    @Test
    fun negative_values_cant_be_parsed() {
        assertFailsWith<NumberFormatException> { DiscordBitSet("-1") }
        assertFailsWith<NumberFormatException> { DiscordBitSet("-99999999999999999999999999999999") }
    }

    private val numberStrings = listOf("0", "1", "1024", "6543654", "59946645771238946")

    // https://github.com/kordlib/kord/issues/911
    @Test
    fun deserialization_works_with_json_strings_and_numbers() {
        numberStrings.forEach { number ->
            val string = "\"$number\""
            val expected = DiscordBitSet(number)
            assertEquals(expected, Json.decodeFromString(string))
            assertEquals(expected, Json.decodeFromString(number))
        }
    }

    @Test
    fun serialization_works_and_produces_json_strings() {
        numberStrings.forEach { number ->
            val bitSet = DiscordBitSet(number)
            val string = Json.encodeToString(bitSet)
            val json = Json.encodeToJsonElement(bitSet)
            assertEquals("\"$number\"", string)
            assertIs<JsonPrimitive>(json)
            assertTrue(json.isString)
            assertEquals(number, json.content)
        }
    }
}
