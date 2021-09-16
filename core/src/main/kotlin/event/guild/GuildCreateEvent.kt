package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

class GuildCreateEvent(val guild: Guild, override val shard: Int) : Event {
    override val kord: Kord get() = guild.kord
    override val guildId: Snowflake
        get() = guild.id

    override fun toString(): String {
        return "GuildCreateEvent(guild=$guild, shard=$shard)"
    }
}