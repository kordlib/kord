package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Member
import dev.kord.core.live.*
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildMemberUpdate
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
class LiveMemberTest : AbstractLiveEntityTest<LiveMember>() {

    private lateinit var userId: Snowflake

    override fun onBeforeAll() {
        super.onBeforeAll()
        userId = Snowflake(0)
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveMember(
            Member(
                kord = kord,
                memberData = MemberData(
                    userId = userId,
                    guildId = guildId,
                    roles = emptyList(),
                    joinedAt = "",
                    premiumSince = Optional.Missing()
                ),
                userData = UserData(
                    id = userId,
                    username = "",
                    discriminator = ""
                )
            )
        )
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(guildId, it.guildId)
                assertEquals(userId, it.member.id)
                countDown()
            }

            fun createEvent(guildId: Snowflake, userId: Snowflake) = GuildMemberUpdate(
                DiscordUpdatedGuildMember(
                    guildId = guildId,
                    roles = emptyList(),
                    user = DiscordUser(
                        id = userId,
                        username = "",
                        discriminator = "",
                        avatar = null
                    ),
                    joinedAt = ""
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), userId)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(guildId, randomId())
            sendEvent(eventRandomMessage)

            val event = createEvent(guildId, userId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onLeave is called when event is received`() {
        countdownContext(1) {
            live.onLeave {
                assertEquals(guildId, it.guildId)
                assertEquals(userId, it.user.id)
                countDown()
            }

            fun createEvent(guildId: Snowflake, userId: Snowflake) = GuildMemberUpdate(
                DiscordUpdatedGuildMember(
                    guildId = guildId,
                    roles = emptyList(),
                    user = DiscordUser(
                        id = userId,
                        username = "",
                        discriminator = "",
                        avatar = null
                    ),
                    joinedAt = ""
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), userId)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(guildId, randomId())
            sendEvent(eventRandomMessage)

            val event = createEvent(guildId, userId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when the member is banned`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(guildId: Snowflake, userId: Snowflake) = GuildBanAdd(
                DiscordGuildBan(
                    guildId = guildId.asString,
                    user = DiscordUser(
                        id = userId,
                        username = "",
                        discriminator = "",
                        avatar = null
                    )
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId(), userId)
            sendEvent(eventRandomGuild)

            assertTrue { live.isActive }

            val eventRandomUser = createEvent(guildId, randomId())
            sendEvent(eventRandomUser)

            assertTrue { live.isActive }

            val event = createEvent(guildId, userId)
            sendEvent(event)

            assertFalse { live.isActive }
        }
    }

    @Test
    fun `Check onShutdown is called when the guild is deleted`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(guildId: Snowflake) = GuildDelete(
                DiscordUnavailableGuild(
                    id = guildId
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId())
            sendEvent(eventRandomGuild)

            assertTrue { live.isActive }

            val event = createEvent(guildId)
            sendEvent(event)

            assertFalse { live.isActive }
        }
    }
}