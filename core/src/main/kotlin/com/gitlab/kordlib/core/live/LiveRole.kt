package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent
import com.gitlab.kordlib.core.event.role.RoleDeleteEvent
import com.gitlab.kordlib.core.event.role.RoleUpdateEvent

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