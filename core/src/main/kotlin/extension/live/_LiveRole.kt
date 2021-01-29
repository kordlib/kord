package dev.kord.core.extension.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Role
import dev.kord.core.event.Event
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent
import dev.kord.core.live.LiveRole
import dev.kord.core.live.live
import dev.kord.core.live.on

@KordPreview
inline fun Role.live(block: LiveRole.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveRole.delete(block: suspend (RoleDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveRole.update(block: suspend (RoleUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveRole.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is RoleDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveRole.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
