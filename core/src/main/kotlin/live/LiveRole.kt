package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.event.Event
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent

@KordPreview
fun Role.live() = LiveRole(this)

@KordPreview
class LiveRole(role: Role) : AbstractLiveEntity(), Entity by role {
    var role = role
        private set

    override fun filter(event: Event) = when (event) {
        is RoleDeleteEvent -> role.id == event.roleId
        is RoleUpdateEvent -> role.id == event.role.id
        is GuildDeleteEvent -> role.guildId == event.guildId
        else -> false
    }

    override fun update(event: Event) = when (event) {
        is RoleDeleteEvent -> shutDown()
        is GuildDeleteEvent -> shutDown()
        is RoleUpdateEvent -> role = event.role
        else -> Unit
    }

}