package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event

/**
 * The event dispatched in 3 scenarios:
 * 1. A user is initially connecting, tp lazily load and back-fill information for all unavailable guilds sent in the Ready
 * event. Guilds that are unavailable due to an outage will send a [GuildDeleteEvent]
 * 2. When a Guild becomes available again to the client
 * 3. When the current user joins a new Guild.
 *
 *  _Scenarios 1 and 3 may be marked unavailable during an outage_
 *
 *  See [Guild Create](https://discord.com/developers/docs/topics/gateway-events#guild-create)
 */
public class GuildCreateEvent(
    public val guild: Guild,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = guild.kord

    override fun toString(): String {
        return "GuildCreateEvent(guild=$guild, shard=$shard)"
    }
}
