package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

class GuildDeleteEvent(
    override val guildId: Snowflake,
    val unavailable: Boolean,
    val guild: Guild?,
    override val kord: Kord,
    override val shard: Int
) : Event {

    override fun toString(): String {
        return "GuildDeleteEvent(guildId=$guildId, unavailable=$unavailable, guild=$guild, kord=$kord, shard=$shard)"
    }

}
