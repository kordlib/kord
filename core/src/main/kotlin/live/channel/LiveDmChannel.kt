package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.DMChannelCreateEvent
import dev.kord.core.event.channel.DMChannelDeleteEvent
import dev.kord.core.event.channel.DMChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.on

@KordPreview
fun DmChannel.live() = LiveDmChannel(this)

@KordPreview
inline fun DmChannel.live(block: LiveDmChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveDmChannel.onCreate(block: suspend (DMChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveDmChannel.onUpdate(block: suspend (DMChannelUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
inline fun LiveDmChannel.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is DMChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveDmChannel.onDelete(block: suspend (DMChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveDmChannel.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveDmChannel(channel: DmChannel) : LiveChannel(), KordEntity by channel {

    override var channel: DmChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is DMChannelCreateEvent -> channel = event.channel
        is DMChannelUpdateEvent -> channel = event.channel
        is DMChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}
