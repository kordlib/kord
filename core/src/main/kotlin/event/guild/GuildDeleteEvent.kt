package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

public class GuildDeleteEvent(
    public val guildId: Snowflake,
    public val unavailable: Boolean,
    public val guild: Guild?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {

    override fun toString(): String {
        return "GuildDeleteEvent(guildId=$guildId, unavailable=$unavailable, guild=$guild, kord=$kord, shard=$shard)"
    }

}
