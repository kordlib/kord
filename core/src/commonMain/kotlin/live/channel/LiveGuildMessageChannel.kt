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
public fun TopGuildMessageChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveGuildMessageChannel = LiveGuildMessageChannel(this, coroutineScope)

@KordPreview
public inline fun TopGuildMessageChannel.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveGuildMessageChannel.() -> Unit
): LiveGuildMessageChannel = this.live(coroutineScope).apply(block)

@KordPreview
public fun LiveGuildMessageChannel.onUpdate(scope: CoroutineScope = this, block: suspend (ChannelUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveGuildMessageChannel(
    channel: TopGuildMessageChannel,
    coroutineScope: CoroutineScope = channel.kord + SupervisorJob(channel.kord.coroutineContext.job)
) : LiveChannel(channel.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = channel.id

    override var channel: TopGuildMessageChannel = channel
        private set

    override fun update(event: Event): Unit = when (event) {
        is ChannelCreateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelUpdateEvent -> channel = event.channel as TopGuildMessageChannel
        is ChannelDeleteEvent -> shutDown(LiveCancellationException(event, "The channel is deleted"))

        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))

        else -> Unit
    }

}
