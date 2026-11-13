package dev.kord.common.entity

import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.*

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

    @Test
    fun copy_does_not_modify_original() {
        val a = Permissions()
        val b = a.copy { /* no modification */ }
        val c = a.copy { +Permission.Administrator }
        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(b, c)
    }

    @Test
    fun modifying_Builder_after_build_does_not_affect_built_instances() {
        val builder = Permissions.Builder()
        val a = builder.build()
        builder.apply { +Permission.Administrator }
        val b = builder.build()
        val c = builder.build()
        assertNotEquals(a, b)
        assertNotEquals(a, c)
        assertEquals(b, c)
    }
}
