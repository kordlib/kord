package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.Instant

class MemberUpdateEvent internal constructor(
        val old: Member?,
        val guildId: Snowflake,
        val memberId: Snowflake,
        val currentRoleIds: Set<Snowflake>,
        val currentNickName: String,
        val premiumSince: Instant?,
        override val kord: Kord
) : Event {

    val member: MemberBehavior get() = MemberBehavior(guildId, memberId, kord)

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val currentRoles: Flow<Role> get() = currentRoleIds.asFlow().map { kord.getRole(guildId, it) }.filterNotNull()

    suspend fun getMember(): Member = kord.getMember(guildId, memberId)!!

    suspend fun getGuild(): Guild = member.getGuild()

}