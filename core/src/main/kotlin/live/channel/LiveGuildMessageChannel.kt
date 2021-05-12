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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job

@KordPreview
fun GuildMessageChannel.live(dispatcher: CoroutineDispatcher = Dispatchers.Default) =
    LiveGuildMessageChannel(this, dispatcher)

@KordPreview
inline fun GuildMessageChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: LiveGuildMessageChannel.() -> Unit
) = this.live(dispatcher).apply(block)

@Deprecated(
    "The block is never called because the channel is already created",
    ReplaceWith("LiveGuild.onChannelCreate")
)
@KordPreview
fun LiveGuildMessageChannel.onCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildMessageChannel.onUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the live entity is shutdown",
    ReplaceWith("LiveGuildMessageChannel.onShutDown((() -> Unit)?)")
)
@KordPreview
inline fun LiveGuildMessageChannel.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is ChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("LiveGuildMessageChannel.onShutDown((() -> Unit)?)")
)
@KordPreview
fun LiveGuildMessageChannel.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("LiveGuildMessageChannel.onShutDown((() -> Unit)?)")
)
@KordPreview
fun LiveGuildMessageChannel.onDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveGuildMessageChannel(
    channel: GuildMessageChannel,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : LiveChannel(dispatcher, channel.kord.coroutineContext.job), KordEntity by channel {

    override var channel: GuildMessageChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelDeleteEvent -> shutdown()

        is GuildDeleteEvent -> shutdown()

        else -> Unit
    }

}
