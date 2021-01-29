package dev.kord.core.extension.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.channel.LiveGuildMessageChannel
import dev.kord.core.live.channel.live
import dev.kord.core.live.on

@KordPreview
inline fun GuildMessageChannel.live(block: LiveGuildMessageChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveGuildMessageChannel.create(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildMessageChannel.update(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuildMessageChannel.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is ChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveGuildMessageChannel.channelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildMessageChannel.delete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
