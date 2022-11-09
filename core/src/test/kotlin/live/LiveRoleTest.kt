package live

import BoxedSnowflake
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.cache.data.RoleData
import dev.kord.core.entity.Role
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.live.LiveRole
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.onUpdate
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildRoleDelete
import dev.kord.gateway.GuildRoleUpdate
import equality.randomId
import kotlinx.coroutines.job
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
class LiveRoleTest : AbstractLiveEntityTest<LiveRole>() {

    private lateinit var roleId: BoxedSnowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        roleId = BoxedSnowflake(randomId())
    }

    @BeforeTest
    fun onBefore() {
        live = LiveRole(
            Role(
                kord = kord,
                data = RoleData(
                    id = roleId.value,
                    guildId = guildId.value,
                    name = "test",
                    color = 0,
                    hoisted = false,
                    icon = Optional.Missing(),
                    unicodeEmoji = Optional.Missing(),
                    position = 0,
                    permissions = Permissions(Permission.CreateInstantInvite),
                    managed = false,
                    mentionable = false
                )
            )
        )
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(roleId.value, it.role.id)
                count()
            }

            sendEventValidAndRandomId(roleId.value) {
                GuildRoleUpdate(
                    DiscordGuildRole(
                        guildId = randomId(),
                        role = DiscordRole(
                            id = it,
                            name = "",
                            color = 0,
                            hoist = false,
                            icon = Optional.Missing(),
                            unicodeEmoji = Optional.Missing(),
                            position = 0,
                            permissions = Permissions(Permission.BanMembers),
                            managed = false,
                            mentionable = false
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check if live entity is completed when the role is deleted`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as RoleDeleteEvent
                assertEquals(roleId.value, event.roleId)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(roleId.value) {
                GuildRoleDelete(
                    DiscordDeletedGuildRole(
                        guildId = randomId(),
                        id = it
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
                assertEquals(guildId.value, event.guildId)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId.value) {
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
