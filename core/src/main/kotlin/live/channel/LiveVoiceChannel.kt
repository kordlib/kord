package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.VoiceChannelCreateEvent
import dev.kord.core.event.channel.VoiceChannelDeleteEvent
import dev.kord.core.event.channel.VoiceChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@KordPreview
fun VoiceChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord
) = LiveVoiceChannel(this, dispatcher, parent)

@KordPreview
inline fun VoiceChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord,
    block: LiveVoiceChannel.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveVoiceChannel.onCreate(scope: CoroutineScope = this, block: suspend (VoiceChannelCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveVoiceChannel.onUpdate(scope: CoroutineScope = this, block: suspend (VoiceChannelUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveVoiceChannel.onShutDown(scope: CoroutineScope = this, crossinline block: suspend (Event) -> Unit) =
    on<Event>(scope) {
        if (it is VoiceChannelDeleteEvent || it is GuildDeleteEvent) {
            block(it)
        }
    }

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveVoiceChannel.onDelete(scope: CoroutineScope = this, block: suspend (VoiceChannelDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveVoiceChannel.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveVoiceChannel(
    channel: VoiceChannel,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = channel.kord
) : LiveChannel(channel.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: VoiceChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is VoiceChannelCreateEvent -> channel = event.channel
        is VoiceChannelUpdateEvent -> channel = event.channel
        is VoiceChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
