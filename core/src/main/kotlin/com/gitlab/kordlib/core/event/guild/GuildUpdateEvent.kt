package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.event.Event

class GuildUpdateEvent internal constructor(val guild: Guild) : Event {
    override val kord: Kord get() = guild.kord
}