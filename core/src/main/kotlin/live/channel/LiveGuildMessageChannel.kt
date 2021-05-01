package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.on

@KordPreview
fun GuildMessageChannel.live() = LiveGuildMessageChannel(this)

@KordPreview
inline fun GuildMessageChannel.live(block: LiveGuildMessageChannel.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveGuildMessageChannel.onCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildMessageChannel.onUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "This method cannot intercept a manual shutdown",
    replaceWith = ReplaceWith("AbstractLiveKordEntity.onShutDown(() -> Unit)")
)
@KordPreview
inline fun LiveGuildMessageChannel.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is ChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@KordPreview
fun LiveGuildMessageChannel.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildMessageChannel.onDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveGuildMessageChannel(channel: GuildMessageChannel) : LiveChannel(), KordEntity by channel {

    override var channel: GuildMessageChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}
