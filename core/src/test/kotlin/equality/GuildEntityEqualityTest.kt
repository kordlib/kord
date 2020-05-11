package equality

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Entity
import kotlin.test.Test
import kotlin.test.assertNotEquals

interface GuildEntityEqualityTest<T: Entity> : EntityEqualityTest<T> {

    fun newEntity(id: Snowflake, guildId: Snowflake): T

    override fun newEntity(id: Snowflake): T = newEntity(id, Snowflake(1))

    @Test
    fun `Guild Entity with different guild ids are not equal`() {
        val id = randomId()
        val a = newEntity(id, randomId())
        val b = newEntity(id, randomId())

        assertNotEquals(a, b)
    }

    @Test
    fun `Guild Entity with different ids but same guild id are not equal`() {
        val id = randomId()
        val a = newEntity(randomId(), id)
        val b = newEntity(randomId(), id)

        assertNotEquals(a, b)
    }

    companion object {
        operator fun<T: Entity> invoke(supplier: (id: Snowflake, guildId: Snowflake) -> T) = object: GuildEntityEqualityTest<T> {
            override fun newEntity(id: Snowflake, guildId: Snowflake): T = supplier(id, guildId)
        }
    }
}