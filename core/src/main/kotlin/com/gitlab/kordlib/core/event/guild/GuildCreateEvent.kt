package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.event.Event

class GuildCreateEvent (val guild: Guild, override val shard: Int) : Event {
    override val kord: Kord get() = guild.kord

    override fun toString(): String {
        return "GuildCreateEvent(guild=$guild, shard=$shard)"
    }
}