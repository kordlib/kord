package dev.kord.core.live.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.randomId
import dev.kord.gateway.ChannelUpdate
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class LiveDmChannelTest : LiveChannelTest<LiveDmChannel>() {

    override val channelId: Snowflake = randomId()

    @BeforeTest
    fun onBefore() = runTest {
        live = LiveDmChannel(
            DmChannel(
                kord = kord,
                data = ChannelData(
                    id = channelId,
                    type = ChannelType.DM,
                    guildId = guildId.optionalSnowflake()
                )
            )
        )
    }

    @Test
    @JsName("test31")
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
                        type = ChannelType.DM,
                    ),
                    0
                )
            }
        }
    }
}
