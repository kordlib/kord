package liveEntity.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordUnavailableGuild
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.live.channel.*
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.ChannelDelete
import dev.kord.gateway.ChannelUpdate
import dev.kord.gateway.GuildDelete
import kotlinx.coroutines.runBlocking
import liveEntity.AbstractLiveEntityTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveVoiceChannelTest : LiveChannelTest<LiveVoiceChannel>() {

    override lateinit var channelId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        channelId = Snowflake(0)
    }

    @BeforeTest
    fun onBefore() = runBlocking {
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