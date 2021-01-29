package dev.kord.core.extension.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.DMChannelCreateEvent
import dev.kord.core.event.channel.DMChannelDeleteEvent
import dev.kord.core.event.channel.DMChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.channel.LiveDmChannel
import dev.kord.core.live.channel.live
import dev.kord.core.live.on

@KordPreview
inline fun DmChannel.live(block: LiveDmChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveDmChannel.create(block: suspend (DMChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveDmChannel.update(block: suspend (DMChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveDmChannel.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is DMChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveDmChannel.delete(block: suspend (DMChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveDmChannel.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
