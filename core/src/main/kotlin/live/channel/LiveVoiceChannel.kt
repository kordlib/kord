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
import kotlinx.coroutines.*

/**
 * Returns a [LiveVoiceChannel] for a given [VoiceChannel].
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveChannel] with
 * @return the created [LiveVoiceChannel]
 */
@KordPreview
public fun VoiceChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveVoiceChannel = LiveVoiceChannel(this, coroutineScope)

/**
 * Returns a [LiveVoiceChannel] for a given [VoiceChannel] with configuration.
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveChannel] with
 * @param block The [LiveVoiceChannel] configuration
 * @return the created [LiveVoiceChannel]
 */
@KordPreview
public inline fun VoiceChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveVoiceChannel.() -> Unit
): LiveVoiceChannel = this.live(coroutineScope).apply(block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveVoiceChannel.onUpdate(
    scope: CoroutineScope = this,
    block: suspend (VoiceChannelUpdateEvent) -> Unit
): Job =
    on(scope = scope, consumer = block)

/**
 * A live entity for a [VoiceChannel]
 *
 * @property channel The [VoiceChannel] to get a live object for
 * @property coroutineScope The [CoroutineScope] to create the [LiveChannel] with
 */
@KordPreview
public class LiveVoiceChannel(
    channel: VoiceChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: VoiceChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is VoiceChannelCreateEvent -> channel = event.channel
        is VoiceChannelUpdateEvent -> channel = event.channel
        is VoiceChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
