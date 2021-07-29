package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.*

@KordPreview
fun TopGuildMessageChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
) = LiveGuildMessageChannel(this, coroutineScope)

@KordPreview
inline fun TopGuildMessageChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveGuildMessageChannel.() -> Unit
) = this.live(coroutineScope).apply(block)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    "The block is never called because the channel is already created, use LiveGuild.onChannelCreate(block)",
    level = DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildMessageChannel.onCreate(scope: CoroutineScope = this, block: suspend (ChannelCreateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveGuildMessageChannel.onUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveGuildMessageChannel.onShutDown(
    scope: CoroutineScope = this,
    crossinline block: suspend (Event) -> Unit
) = on<Event>(scope) {
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
fun LiveGuildMessageChannel.onChannelDelete(scope: CoroutineScope = this, block: suspend (ChannelDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveGuildMessageChannel.onDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveGuildMessageChannel(
    channel: TopGuildMessageChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: TopGuildMessageChannel = channel
        private set

    override fun update(event: Event) = when (event) {
        is ChannelCreateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
