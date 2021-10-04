package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext

public class GuildCreateEvent(
    public val guild: Guild,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = guild.kord.coroutineContext,
) : Event {
    override val kord: Kord get() = guild.kord

    override fun toString(): String {
        return "GuildCreateEvent(guild=$guild, shard=$shard)"
    }
}
