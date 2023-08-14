package dev.kord.common.entity

import dev.kord.ksp.Generate.EntityType.BIT_SET_FLAGS
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/** Test representative for generated [BIT_SET_FLAGS]. */
class PermissionTest {
    @Test
    fun values_returns_expected_flags() {
        val expected = Permission.entries.map { Permission.fromShift(Random.nextInt(0..99)) }.toSet()
        assertEquals(expected, actual = Permissions(expected).values)
        assertEquals(expected = Permission.entries.toSet(), actual = Permissions(Permission.entries).values)
    }

    @Test
    fun fromShift_returns_same_object_for_known_entries() {
        for (perm in Permission.entries) {
            assertSame(expected = perm, actual = Permission.fromShift(perm.shift))
        }
    }

    @Test
    fun fromShift_throws_IAE() {
        for (shift in (-100..-1) + Int.MIN_VALUE) {
            assertFailsWith<IllegalArgumentException> { Permission.fromShift(shift) }
        }
    }
}
