package equality

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.entity.Entity
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