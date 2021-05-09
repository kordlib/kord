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
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.live.channel.LiveCategory
import dev.kord.core.live.channel.LiveDmChannel
import dev.kord.core.live.channel.LiveGuildChannel
import dev.kord.core.live.channel.onUpdate
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.ChannelDelete
import dev.kord.gateway.ChannelUpdate
import dev.kord.gateway.GuildDelete
import equality.randomId
import kotlinx.coroutines.runBlocking
import liveEntity.AbstractLiveEntityTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveGuildChannelTest : LiveChannelTest<LiveGuildChannel>() {

    inner class GuildChannelMock(
        override val kord: Kord,
        override val data: ChannelData,
        override val supplier: EntitySupplier = kord.defaultSupplier
    ) : GuildMessageChannel {
        override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannel {
            error("Not invoked in test")
        }
    }

    override lateinit var channelId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        channelId = randomId()
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveGuildChannel(
            GuildChannelMock(
                kord = kord,
                data = ChannelData(
                    id = channelId,
                    type = ChannelType.GuildCategory,
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
                        type = ChannelType.GuildCategory,
                    ),
                    0
                )
            }
        }
    }
}