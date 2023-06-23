package dev.kord.core.live

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.core.cache.data.RoleData
import dev.kord.core.entity.Role
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.randomId
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.GuildRoleDelete
import dev.kord.gateway.GuildRoleUpdate
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class LiveRoleTest : AbstractLiveEntityTest<LiveRole>() {

    private val roleId: Snowflake = randomId()


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
    @JsName("test1")
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
    @JsName("test2")
    fun `Check if live entity is completed when the role is deleted`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as RoleDeleteEvent
                assertEquals(roleId, event.roleId)
                runTest {
                    count()
                }
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
    @JsName("test3")
    fun `Check if live entity is completed when the guild is deleted`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId, event.guildId)
                runTest {
                    count()
                }
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
