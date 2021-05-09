package liveEntity.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordUnavailableGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.*
import dev.kord.core.live.channel.*
import dev.kord.gateway.ChannelDelete
import dev.kord.gateway.GuildDelete
import equality.randomId
import kotlinx.coroutines.runBlocking
import liveEntity.AbstractLiveEntityTest
import org.junit.jupiter.api.TestInstance
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
abstract class LiveChannelTest<LIVE: LiveChannel> : AbstractLiveEntityTest<LIVE>() {

    protected abstract val channelId: Snowflake

    @Test
    fun `Check type of live entity corresponds to the channel type`() = runBlocking {
        val data = ChannelData(
            id = randomId(),
            type = ChannelType.DM
        )

        fun checkLiveEntityType(expectedType: KClass<*>, channel: Channel) {
            assertEquals(expectedType, channel.live()::class)
        }
        checkLiveEntityType(LiveDmChannel::class, DmChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildMessageChannel::class, NewsChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildChannel::class, StoreChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildMessageChannel::class, TextChannel(kord = kord, data = data))
        checkLiveEntityType(LiveVoiceChannel::class, VoiceChannel(kord = kord, data = data))
    }

    @Test
    fun `Check onShutdown is called when event the category delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomId(channelId) {
                ChannelDelete(
                    DiscordChannel(
                        id = it,
                        type = live.channel.type,
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onShutdown is called when event the guild delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId) {
                GuildDelete(
                    DiscordUnavailableGuild(
                        id = it
                    ),
                    0
                )
            }
        }
    }
}