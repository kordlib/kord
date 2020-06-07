package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.event.Event

class IntegrationsUpdateEvent internal constructor(val guildId: Snowflake, override val kord: Kord) : Event {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild() = kord.getGuild(guildId)!!

}