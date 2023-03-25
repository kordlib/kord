package live.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordUnavailableGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.*
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.channel.*
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.gateway.ChannelDelete
import dev.kord.gateway.GuildDelete
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import live.AbstractLiveEntityTest
import randomId
import kotlin.js.JsName
import kotlin.reflect.KClass
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
abstract class LiveChannelTest<LIVE : LiveChannel> : AbstractLiveEntityTest<LIVE>() {

    protected abstract val channelId: Snowflake

    @Test
    @JsName("test41")
    fun `Check type of live entity corresponds to the channel type`() = runTest {
        val data = ChannelData(
            id = randomId(),
            type = ChannelType.DM
        )

        fun checkLiveEntityType(expectedType: KClass<*>, channel: Channel) {
            assertEquals(expectedType, channel.live()::class)
        }

        checkLiveEntityType(LiveDmChannel::class, DmChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildMessageChannel::class, NewsChannel(kord = kord, data = data))
        checkLiveEntityType(LiveGuildMessageChannel::class, TextChannel(kord = kord, data = data))
        checkLiveEntityType(LiveVoiceChannel::class, VoiceChannel(kord = kord, data = data))
    }

    @Test
    @JsName("test2")
    fun `Check if live entity is completed when event the category delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as ChannelDeleteEvent
                assertEquals(channelId, event.channel.id)
                runTest {
                    count()
                }
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
    @JsName("test3")
    fun `Check if live entity is completed when event the guild delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId, event.guildId)
                runTest {
                    count()
                }
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
