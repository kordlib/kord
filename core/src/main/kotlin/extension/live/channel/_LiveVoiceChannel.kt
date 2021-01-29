package dev.kord.core.extension.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.VoiceChannelCreateEvent
import dev.kord.core.event.channel.VoiceChannelDeleteEvent
import dev.kord.core.event.channel.VoiceChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.channel.LiveVoiceChannel
import dev.kord.core.live.channel.live
import dev.kord.core.live.on

@KordPreview
inline fun VoiceChannel.live(block: LiveVoiceChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveVoiceChannel.create(block: suspend (VoiceChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveVoiceChannel.update(block: suspend (VoiceChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveVoiceChannel.shutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is VoiceChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveVoiceChannel.delete(block: suspend (VoiceChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveVoiceChannel.guildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)
