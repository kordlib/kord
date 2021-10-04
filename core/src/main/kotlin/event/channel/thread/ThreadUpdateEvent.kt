package dev.kord.core.event.channel.thread

import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.ChannelUpdateEvent
import kotlin.coroutines.CoroutineContext

public sealed interface ThreadUpdateEvent : ChannelUpdateEvent {
    override val channel: ThreadChannel
}


public class TextChannelThreadUpdateEvent(
    override val channel: TextChannelThread,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = channel.kord.coroutineContext,
) :
    ThreadUpdateEvent {
    override fun toString(): String {
        return "TextThreadChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}


public class NewsChannelThreadUpdateEvent(
    override val channel: NewsChannelThread,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = channel.kord.coroutineContext,
) :
    ThreadUpdateEvent {
    override fun toString(): String {
        return "NewsThreadChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}


public class UnknownChannelThreadUpdateEvent(
    override val channel: ThreadChannel,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = channel.kord.coroutineContext,
) :
    ThreadUpdateEvent {
    override fun toString(): String {
        return "UnknownChannelThreadUpdateEvent(channel=$channel, shard=$shard)"
    }
}
