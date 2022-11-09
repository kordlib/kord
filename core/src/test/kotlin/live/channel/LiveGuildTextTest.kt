package live.channel

import BoxedSnowflake
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

    override lateinit var channelId: BoxedSnowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        channelId = BoxedSnowflake(randomId())
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveGuildChannel(
            GuildChannelMock(
                kord = kord,
                data = ChannelData(
                    id = channelId.value,
                    type = ChannelType.GuildText,
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
                        type = ChannelType.GuildText,
                    ),
                    0
                )
            }
        }
    }
}
