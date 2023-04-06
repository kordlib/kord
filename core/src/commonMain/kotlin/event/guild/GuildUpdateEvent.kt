package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

/**
 * The event dispatched when a [Guild] is updated.
 *
 * The [old][old] [Guild] may be `null` unless the guild has been stored in the cache.
 *
 * See [Guild Update](https://discord.com/developers/docs/topics/gateway-events#guild-update)
 */
public class GuildUpdateEvent(
    public val guild: Guild,
    public val old: Guild?,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = guild.kord
    override fun toString(): String {
        return "GuildUpdateEvent(guild=$guild, shard=$shard)"
    }
}
