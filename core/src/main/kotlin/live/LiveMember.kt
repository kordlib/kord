package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Member
import dev.kord.core.event.Event
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.live.channel.LiveGuildChannel

@KordPreview
fun Member.live() = LiveMember(this)

@KordPreview
inline fun Member.live(block: LiveMember.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveMember.onLeave(block: suspend (MemberLeaveEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMember.onUpdate(block: suspend (MemberUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMember.onBanAdd(block: suspend (BanAddEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuildChannel.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is MemberLeaveEvent || it is BanAddEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveGuildChannel.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveMember(member: Member) : AbstractLiveKordEntity(), KordEntity by member {
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
