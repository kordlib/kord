package com.gitlab.kordlib.core.event.role

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.event.Event

class RoleCreateEvent internal constructor(val role: Role) : Event {

    override val kord: Kord get() = role.kord

    val guildId: Snowflake get() = role.guildId

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild() = kord.getGuild(guildId)!!

}