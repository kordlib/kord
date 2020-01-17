package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.event.Event

 class MemberChunksEvent internal constructor(
         val guildId: Snowflake,
         val members: Set<Member>,
         override val kord: Kord
) : Event {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild() = kord.getGuild(guildId)!!

}