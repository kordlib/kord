package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.guild.BanEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent
import com.gitlab.kordlib.core.event.guild.MemberLeaveEvent
import com.gitlab.kordlib.core.event.guild.MemberUpdateEvent

@KordPreview
fun Member.live() = LiveMember(this)

@KordPreview
class LiveMember(member: Member) : AbstractLiveEntity(), Entity by member {
    var member = member
        private set

    override fun filter(event: Event) = when (event) {
        is MemberLeaveEvent -> member.id == event.user.id
        is MemberUpdateEvent -> member.id == event.memberId
        is BanEvent -> member.id == event.user.id
        is GuildDeleteEvent -> member.guildId == event.guildId
        else -> false

    }


    override fun update(event: Event) = when (event) {
        is MemberLeaveEvent -> shutDown()
        is BanEvent -> shutDown()
        is GuildDeleteEvent -> shutDown()
        is MemberUpdateEvent -> member = Member(member.memberData.copy(
                nick = event.currentNickName,
                roles = event.currentRoleIds.map { it.value }), member.data, kord)

        else -> Unit
    }
}