package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.event.Event

class MemberJoinEvent internal constructor(val member: Member) : Event {

    override val kord: Kord get() = member.kord

    val guildId: Snowflake get() = member.guildId

    val guild: GuildBehavior get() = member.guild

    suspend fun getGuild(): Guild = member.getGuild()

}