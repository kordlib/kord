package live.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.live.channel.LiveGuildChannel
import dev.kord.core.live.channel.onUpdate
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.ChannelUpdate
import kotlinx.coroutines.test.runTest
import randomId
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class LiveGuildTextTest : LiveChannelTest<LiveGuildChannel>() {

    inner class GuildChannelMock(
        override val kord: Kord,
        override val data: ChannelData,
        override val supplier: EntitySupplier = kord.defaultSupplier
    ) : TopGuildChannel {
        override fun withStrategy(strategy: EntitySupplyStrategy<*>): TopGuildChannel {
            error("Not invoked in test")
        }
    }

    override val channelId: Snowflake = randomId()

    @BeforeTest
    fun onBefore() = runTest {
        live = LiveGuildChannel(
            GuildChannelMock(
                kord = kord,
                data = ChannelData(
                    id = channelId,
                    type = ChannelType.GuildText,
                    guildId = guildId.optionalSnowflake()
                )
            )
        )
    }

    @Test
    @JsName("testOnUpdate")
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(it.channel.id, channelId)
                count()
            }

            sendEventValidAndRandomId(channelId) {
                ChannelUpdate(
                    DiscordChannel(
                        id = it,
                        type = ChannelType.GuildText,
                    ),
                    0
                )
            }
        }
    }
}
