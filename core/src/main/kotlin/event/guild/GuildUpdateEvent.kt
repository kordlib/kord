package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

class GuildUpdateEvent(val guild: Guild, override val shard: Int) : Event {
    override val kord: Kord get() = guild.kord
    override fun toString(): String {
        return "GuildUpdateEvent(guild=$guild, shard=$shard)"
    }
}
