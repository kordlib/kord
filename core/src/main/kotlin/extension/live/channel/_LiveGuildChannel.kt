package dev.kord.core.extension.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.channel.LiveGuildChannel
import dev.kord.core.live.channel.live
import dev.kord.core.live.on

@KordPreview
inline fun GuildChannel.live(block: LiveGuildChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveGuildChannel.create(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildChannel.update(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveGuildChannel.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is ChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveGuildChannel.delete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildChannel.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
