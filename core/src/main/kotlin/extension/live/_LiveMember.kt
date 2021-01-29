package dev.kord.core.extension.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Member
import dev.kord.core.event.Event
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.live.LiveMember
import dev.kord.core.live.channel.LiveGuildChannel
import dev.kord.core.live.live
import dev.kord.core.live.on

@KordPreview
inline fun Member.live(block: LiveMember.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveMember.leave(block: suspend (MemberLeaveEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMember.update(block: suspend (MemberUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveMember.banAdd(block: suspend (BanAddEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuildChannel.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is MemberLeaveEvent || it is BanAddEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveGuildChannel.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
