package dev.kord.core.live.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.randomId
import dev.kord.gateway.ChannelUpdate
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class LiveVoiceChannelTest : LiveChannelTest<LiveVoiceChannel>() {

    override val channelId: Snowflake = randomId()

    @BeforeTest
    fun onBefore() = runTest {
        live = LiveVoiceChannel(
            VoiceChannel(
                kord = kord,
                data = ChannelData(
                    id = channelId,
                    type = ChannelType.GuildVoice,
                    guildId = guildId.optionalSnowflake()
                )
            )
        )
    }

    @Test
    @JsName("test21")
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
                        type = ChannelType.GuildVoice,
                    ),
                    0
                )
            }
        }
    }
}
