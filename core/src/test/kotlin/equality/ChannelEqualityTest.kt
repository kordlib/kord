package equality

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.entity.Entity
import mockKord
import kotlin.test.assertEquals

interface ChannelEqualityTest<T: Entity> : EntityEqualityTest<T> {

    @kotlin.test.Test
    fun `Channel is equal to Channel with the same id`() {
        val id = randomId()
        val kord = mockKord()
        val fakeChannel: Entity = ChannelBehavior(id, kord)
        val channel: Entity = newEntity(id)

        assertEquals(fakeChannel, channel)
    }

    companion object {
        operator fun<T: Entity> invoke(supplier: (Snowflake) -> T) = object: ChannelEqualityTest<T> {
            override fun newEntity(id: Snowflake): T = supplier(id)
        }
    }

}