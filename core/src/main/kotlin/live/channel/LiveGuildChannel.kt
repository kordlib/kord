package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@KordPreview
fun GuildChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord
) = LiveGuildChannel(this, dispatcher, parent)

@KordPreview
inline fun GuildChannel.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord,
    block: LiveGuildChannel.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildChannel.onCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuildChannel.onUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveGuildChannel.onShutDown(scope: CoroutineScope = this, crossinline block: suspend (Event) -> Unit) =
    on<Event>(scope) {
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
fun LiveGuildChannel.onDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildChannel.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveGuildChannel(
    channel: GuildChannel,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = channel.kord
) : LiveChannel(channel.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: GuildChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as GuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
