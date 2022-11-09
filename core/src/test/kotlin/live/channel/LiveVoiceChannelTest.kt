package live.channel

import BoxedSnowflake
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.live.channel.LiveVoiceChannel
import dev.kord.core.live.channel.onUpdate
import dev.kord.gateway.ChannelUpdate
import equality.randomId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveVoiceChannelTest : LiveChannelTest<LiveVoiceChannel>() {

    override lateinit var channelId: BoxedSnowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        channelId = BoxedSnowflake(randomId())
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveVoiceChannel(
            VoiceChannel(
                kord = kord,
                data = ChannelData(
                    id = channelId.value,
                    type = ChannelType.GuildVoice,
                    guildId = guildId.value.optionalSnowflake()
                )
            )
        )
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(it.channel.id, channelId.value)
                count()
            }

            sendEventValidAndRandomId(channelId.value) {
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
