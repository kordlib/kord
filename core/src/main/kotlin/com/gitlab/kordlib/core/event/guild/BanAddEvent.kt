package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.Event

class BanAddEvent internal constructor(val user: User, val guildId: Snowflake) : Event {

    override val kord: Kord get() = user.kord

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

}