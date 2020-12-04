package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Member
import dev.kord.core.event.Event
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.event.guild.MemberUpdateEvent

@KordPreview
fun Member.live() = LiveMember(this)

@KordPreview
class LiveMember(member: Member) : AbstractLiveEntity(), Entity by member {
    var member = member
        private set

    override fun filter(event: Event) = when (event) {
        is MemberLeaveEvent -> member.id == event.user.id
        is MemberUpdateEvent -> member.id == event.member.id
        is BanAddEvent -> member.id == event.user.id
        is GuildDeleteEvent -> member.guildId == event.guildId
        else -> false

    }

    override fun update(event: Event) = when (event) {
        is MemberLeaveEvent -> shutDown()
        is BanAddEvent -> shutDown()
        is GuildDeleteEvent -> shutDown()
        is MemberUpdateEvent -> member = event.member

        else -> Unit
    }
}