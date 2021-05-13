package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.cache.data.RoleData
import dev.kord.core.entity.Role
import dev.kord.core.live.LiveRole
import dev.kord.core.live.onUpdate
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildRoleDelete
import dev.kord.gateway.GuildRoleUpdate
import equality.randomId
import kotlinx.coroutines.job
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveRoleTest : AbstractLiveEntityTest<LiveRole>() {

    private lateinit var roleId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        roleId = randomId()
    }

    @BeforeTest
    fun onBefore() {
        live = LiveRole(
            Role(
                kord = kord,
                data = RoleData(
                    id = roleId,
                    guildId = guildId,
                    name = "test",
                    color = 0,
                    hoisted = false,
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
                assertEquals(roleId, it.role.id)
                count()
            }

            sendEventValidAndRandomId(roleId) {
                GuildRoleUpdate(
                    DiscordGuildRole(
                        guildId = randomId(),
                        role = DiscordRole(
                            id = it,
                            name = "",
                            color = 0,
                            hoist = false,
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
    fun `Check onShutdown is called when the role is deleted`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(roleId) {
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
    fun `Check onShutdown is called when the guild is deleted`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
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