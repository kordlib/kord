package dev.kord.core.equality

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.mockKord
import dev.kord.core.randomId
import kotlin.test.Test
import kotlin.test.assertEquals

interface ChannelEqualityTest<T: KordEntity> : EntityEqualityTest<T> {

    @Test
    fun `Channel is equal to Channel with the same id`() {
        val id = randomId()
        val kord = mockKord()
        val fakeChannel: KordEntity = ChannelBehavior(id, kord)
        val channel: KordEntity = newEntity(id)

        assertEquals(fakeChannel, channel)
    }

    companion object {
        operator fun<T: KordEntity> invoke(supplier: (Snowflake) -> T) = object: ChannelEqualityTest<T> {
            override fun newEntity(id: Snowflake): T = supplier(id)
        }
    }

}
