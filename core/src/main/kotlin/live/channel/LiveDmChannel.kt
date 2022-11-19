package dev.kord.core.live.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.DMChannelCreateEvent
import dev.kord.core.event.channel.DMChannelDeleteEvent
import dev.kord.core.event.channel.DMChannelUpdateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import kotlinx.coroutines.*

@KordPreview
public fun DmChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveDmChannel = LiveDmChannel(this, coroutineScope)

@KordPreview
public inline fun DmChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveDmChannel.() -> Unit
): LiveDmChannel = this.live(coroutineScope).apply(block)

@KordPreview
public fun LiveDmChannel.onUpdate(scope: CoroutineScope = this, block: suspend (DMChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveDmChannel(
    channel: DmChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: DmChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is DMChannelCreateEvent -> channel = event.channel
        is DMChannelUpdateEvent -> channel = event.channel
        is DMChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
