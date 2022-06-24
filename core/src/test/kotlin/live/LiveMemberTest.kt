package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Member
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.live.LiveMember
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.onUpdate
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildMemberRemove
import dev.kord.gateway.GuildMemberUpdate
import equality.randomId
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveMemberTest : AbstractLiveEntityTest<LiveMember>() {

    private lateinit var userId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        userId = randomId()
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
                    joinedAt = Instant.fromEpochMilliseconds(0),
                    premiumSince = Optional.Missing(),
                    avatar = Optional.Missing(),
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
                count()
            }

            sendEventValidAndRandomId(userId) {
                GuildMemberUpdate(
                    DiscordUpdatedGuildMember(
                        guildId = randomId(),
                        roles = emptyList(),
                        user = DiscordUser(
                            id = it,
                            username = "",
                            discriminator = "",
                            avatar = null
                        ),
                        joinedAt = Instant.fromEpochMilliseconds(0),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onLeave is called when event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as MemberLeaveEvent
                assertEquals(userId, event.user.id)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(userId) {
                GuildMemberRemove(
                    DiscordRemovedGuildMember(
                        guildId = randomId(),
                        user = DiscordUser(
                            id = it,
                            username = "",
                            discriminator = "",
                            avatar = null
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check if live entity is completed when the member is banned`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as BanAddEvent
                assertEquals(userId, event.user.id)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(userId) {
                GuildBanAdd(
                    DiscordGuildBan(
                        guildId = randomId(),
                        user = DiscordUser(
                            id = it,
                            username = "",
                            discriminator = "",
                            avatar = null
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check if live entity is completed when the guild is deleted`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId, event.guildId)
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
