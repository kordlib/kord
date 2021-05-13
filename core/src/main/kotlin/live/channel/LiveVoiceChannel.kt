package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.VoiceChannelCreateEvent
import dev.kord.core.event.channel.VoiceChannelDeleteEvent
import dev.kord.core.event.channel.VoiceChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.on
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job

@KordPreview
fun VoiceChannel.live(dispatcher: CoroutineDispatcher = Dispatchers.Default) =
    LiveVoiceChannel(this, dispatcher)

@KordPreview
inline fun VoiceChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: LiveVoiceChannel.() -> Unit
) = this.live(dispatcher).apply(block)

@Deprecated(
    "The block is never called because the channel is already created",
    ReplaceWith("LiveGuild.onChannelCreate(block)")
)
@KordPreview
fun LiveVoiceChannel.onCreate(block: suspend (VoiceChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveVoiceChannel.onUpdate(block: suspend (VoiceChannelUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the live entity is shutdown",
    ReplaceWith("onShutDown(block)")
)
@KordPreview
inline fun LiveVoiceChannel.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is VoiceChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("onShutDown(block)")
)
@KordPreview
fun LiveVoiceChannel.onDelete(block: suspend (VoiceChannelDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shutdown",
    ReplaceWith("onShutDown(block)")
)
@KordPreview
fun LiveVoiceChannel.onGuildDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveVoiceChannel(
    channel: VoiceChannel,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : LiveChannel(dispatcher, channel.kord.coroutineContext.job), KordEntity by channel {

    override var channel: VoiceChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is VoiceChannelCreateEvent -> channel = event.channel
        is VoiceChannelUpdateEvent -> channel = event.channel
        is VoiceChannelDeleteEvent -> shutDown()

        is GuildDeleteEvent -> shutDown()

        else -> Unit
    }

}
