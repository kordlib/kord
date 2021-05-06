package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Member
import dev.kord.core.live.LiveMember
import dev.kord.core.live.onLeave
import dev.kord.core.live.onUpdate
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildMemberRemove
import dev.kord.gateway.GuildMemberUpdate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveMemberTest : AbstractLiveEntityTest<LiveMember>() {

    private lateinit var userId: Snowflake

    @BeforeAll
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
                assertEquals(userId, it.member.id)
                countDown()
            }

            fun createEvent(userId: Snowflake) = GuildMemberUpdate(
                DiscordUpdatedGuildMember(
                    guildId = randomId(),
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

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)

            val event = createEvent(userId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onLeave is called when event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(userId: Snowflake) = GuildMemberRemove(
                DiscordRemovedGuildMember(
                    guildId = randomId(),
                    user = DiscordUser(
                        id = userId,
                        username = "",
                        discriminator = "",
                        avatar = null
                    )
                ),
                0
            )

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)
            waitAndCheckLiveIsActive()

            val event = createEvent(userId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }

    @Test
    fun `Check onShutdown is called when the member is banned`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(userId: Snowflake) = GuildBanAdd(
                DiscordGuildBan(
                    guildId = randomId().asString,
                    user = DiscordUser(
                        id = userId,
                        username = "",
                        discriminator = "",
                        avatar = null
                    )
                ),
                0
            )

            val eventRandomUser = createEvent(randomId())
            sendEvent(eventRandomUser)
            waitAndCheckLiveIsActive()

            val event = createEvent(userId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }

    @Test
    fun `Check onShutdown is called when the guild is deleted`() {
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
            waitAndCheckLiveIsActive()

            val event = createEvent(guildId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }
}