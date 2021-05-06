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
        roleId = Snowflake(0)
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
                countDown()
            }

            fun createEvent(roleId: Snowflake) = GuildRoleUpdate(
                DiscordGuildRole(
                    guildId = randomId(),
                    role = DiscordRole(
                        id = roleId,
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

            val eventRandomRole = createEvent(randomId())
            sendEvent(eventRandomRole)

            val event = createEvent(roleId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when the role is deleted`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(roleId: Snowflake) = GuildRoleDelete(
                DiscordDeletedGuildRole(
                    guildId = guildId,
                    id = roleId
                ),
                0
            )

            val eventRandomRole = createEvent(randomId())
            sendEvent(eventRandomRole)
            waitAndCheckLiveIsActive()

            val event = createEvent(roleId)
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