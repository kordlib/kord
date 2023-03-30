package dev.kord.core.equality

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.randomId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

interface EntityEqualityTest<T : KordEntity> {

    fun newEntity(id: Snowflake): T

    @Test
    fun `Entities with the same id are equal`() {
        val id = randomId()
        val a = newEntity(id)
        val b = newEntity(id)
        assertEquals(a, b)
    }

    @Test
    fun `Entities with different ids are not equal`() {
        val a = newEntity(randomId())
        val b = newEntity(randomId())

        assertNotEquals(a, b)
    }

    companion object {
        operator fun <T : KordEntity> invoke(supplier: (Snowflake) -> T) = object : EntityEqualityTest<T> {
            override fun newEntity(id: Snowflake): T = supplier(id)
        }
    }
}
