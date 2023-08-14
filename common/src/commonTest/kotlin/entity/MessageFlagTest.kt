package dev.kord.common.entity

import dev.kord.ksp.Generate.EntityType.BIT_SET_FLAGS
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/** Test representative for generated [BIT_SET_FLAGS]. */
class MessageFlagTest {
    @Test
    fun values_returns_expected_flags() {
        val expected = MessageFlag.entries.map { MessageFlag.fromShift(Random.nextInt(0..30)) }.toSet()
        assertEquals(expected, actual = MessageFlags(expected).values)
        assertEquals(expected = MessageFlag.entries.toSet(), actual = MessageFlags(MessageFlag.entries).values)
    }

    @Test
    fun fromShift_returns_same_object_for_known_entries() {
        for (flag in MessageFlag.entries) {
            assertSame(expected = flag, actual = MessageFlag.fromShift(flag.shift))
        }
    }

    @Test
    fun fromShift_throws_IAE() {
        for (shift in (-100..-1) + (31..100) + Int.MIN_VALUE + Int.MAX_VALUE) {
            assertFailsWith<IllegalArgumentException> { MessageFlag.fromShift(shift) }
        }
    }
}
