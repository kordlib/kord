package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

/**
 * The event dispatched when a guild becomes, or was already, unavailable due to an outage, or when the user leaves/is
 * removed from a guild.
 * If the [unavailable] field was not set, the user was removed from the guild.
 *
 * See [Guild Delete](https://discord.com/developers/docs/topics/gateway-events#guild-delete)
 */
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
