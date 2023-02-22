package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.*

/**
 * Returns a [LiveGuildChannel] for a given [TopGuildChannel].
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveGuildChannel] with
 * @return the created [LiveGuildChannel]
 */
@KordPreview
public fun TopGuildChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveGuildChannel = LiveGuildChannel(this, coroutineScope)

/**
 * Returns a [LiveGuildChannel] for a given [TopGuildChannel] with configuration.
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveGuildChannel] with
 * @param block The [LiveGuildChannel] configuration
 * @return the created [LiveGuildChannel]
 */
@KordPreview
public inline fun TopGuildChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveGuildChannel.() -> Unit
): LiveGuildChannel = this.live(coroutineScope).apply(block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveGuildChannel.onUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * A live entity for a [TopGuildChannel]
 *
 * @property channel The [TopGuildChannel] to get a live object for
 * @property coroutineScope The [CoroutineScope] to create the [LiveChannel] with
 */
@KordPreview
public class LiveGuildChannel(
    channel: TopGuildChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: TopGuildChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is ChannelCreateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
