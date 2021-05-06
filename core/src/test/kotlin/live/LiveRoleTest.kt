package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.cache.data.RoleData
import dev.kord.core.entity.Role
import dev.kord.core.live.LiveRole
import dev.kord.core.live.onUpdate
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildRoleDelete
import dev.kord.gateway.GuildRoleUpdate
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
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
                    permissions = Permissions(Permission.All),
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
                assertEquals(guildId, it.guildId)
                assertEquals(roleId, it.role.id)
                countDown()
            }

            fun createEvent(guildId: Snowflake, roleId: Snowflake) = GuildRoleUpdate(
                DiscordGuildRole(
                    guildId = guildId,
                    role = DiscordRole(
                        id = roleId,
                        name = "",
                        color = 0,
                        hoist = false,
                        position = 0,
                        permissions = Permissions(Permission.All),
                        managed = false,
                        mentionable = false
                    )
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId(), roleId)
            sendEvent(eventRandomGuild)

            val eventRandomRole = createEvent(guildId, randomId())
            sendEvent(eventRandomRole)

            val event = createEvent(guildId, roleId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when the role is deleted`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(guildId: Snowflake, roleId: Snowflake) = GuildRoleDelete(
                DiscordDeletedGuildRole(
                    guildId = guildId,
                    id = roleId
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId(), roleId)
            sendEvent(eventRandomGuild)

            assertTrue { live.isActive }

            val eventRandomRole = createEvent(guildId, randomId())
            sendEvent(eventRandomRole)

            assertTrue { live.isActive }

            val event = createEvent(guildId, roleId)
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