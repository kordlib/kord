package dev.kord.core.event.channel.thread

import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.ChannelUpdateEvent

public sealed interface ThreadUpdateEvent : ChannelUpdateEvent {
    override val channel: ThreadChannel
}


public class TextChannelThreadUpdateEvent(
    override val channel: TextChannelThread,
    override val old: TextChannelThread?,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadUpdateEvent {
    override fun toString(): String {
        return "TextThreadChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}


public class NewsChannelThreadUpdateEvent(
    override val channel: NewsChannelThread,
    override val old: NewsChannelThread?,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadUpdateEvent {
    override fun toString(): String {
        return "NewsThreadChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}


public class UnknownChannelThreadUpdateEvent(
    override val channel: ThreadChannel,
    override val old: ThreadChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadUpdateEvent {
    override fun toString(): String {
        return "UnknownChannelThreadUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}
