package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.job

@KordPreview
fun GuildMessageChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = kord.coroutineContext.job
) = LiveGuildMessageChannel(this, dispatcher, parent)

@KordPreview
inline fun GuildMessageChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = kord.coroutineContext.job,
    block: LiveGuildMessageChannel.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildMessageChannel.onCreate(block: suspend (ChannelCreateEvent) -> Unit) = on(consumer = block)

@KordPreview
fun LiveGuildMessageChannel.onUpdate(block: suspend (ChannelUpdateEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveGuildMessageChannel.onShutDown(crossinline block: suspend (Event) -> Unit) = on<Event> {
    if (it is ChannelDeleteEvent || it is GuildDeleteEvent) {
        block(it)
    }
}

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildMessageChannel.onChannelDelete(block: suspend (ChannelDeleteEvent) -> Unit) = on(consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildMessageChannel.onDelete(block: suspend (GuildDeleteEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveGuildMessageChannel(
    channel: GuildMessageChannel,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = channel.kord.coroutineContext.job
) : LiveChannel(channel.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: GuildMessageChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
