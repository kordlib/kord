package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Role
import dev.kord.core.event.Event
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent

@KordPreview
fun Role.live() = LiveRole(this)

@KordPreview
inline fun Role.live(block: LiveRole.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveRole.onDelete(block: suspend (RoleDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveRole.onUpdate(block: suspend (RoleUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveRole.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is RoleDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveRole.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveRole(role: Role) : AbstractLiveKordEntity(), KordEntity by role {
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
