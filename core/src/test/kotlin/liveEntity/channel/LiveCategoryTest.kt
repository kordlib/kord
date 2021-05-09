package liveEntity.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordUnavailableGuild
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Category
import dev.kord.core.live.channel.LiveCategory
import dev.kord.core.live.channel.onUpdate
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
class LiveCategoryTest : AbstractLiveEntityTest<LiveCategory>() {

    private lateinit var categoryId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        categoryId = Snowflake(0)
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveCategory(
            Category(
                kord = kord,
                data = ChannelData(
                    id = categoryId,
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
                assertEquals(it.channel.id, categoryId)
                count()
            }

            sendEventValidAndRandomId(categoryId) {
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

    @Test
    fun `Check onShutdown is called when event the category delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomId(categoryId) {
                ChannelDelete(
                    DiscordChannel(
                        id = it,
                        type = ChannelType.GuildCategory,
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