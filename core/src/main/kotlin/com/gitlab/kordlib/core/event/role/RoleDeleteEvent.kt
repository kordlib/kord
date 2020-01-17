package com.gitlab.kordlib.core.event.role

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.event.Event

class RoleDeleteEvent internal constructor(
        val guildId: Snowflake,
        val roleId: Snowflake,
        val role: Role?,
        override val kord: Kord
) : Event {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild() = kord.getGuild(guildId)!!

}