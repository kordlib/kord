package dev.kord.core.event.channel.thread

import dev.kord.core.entity.channel.thread.DeletedThreadChannel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.ChannelCreateEvent
import dev.kord.core.event.channel.ChannelDeleteEvent

sealed interface ThreadChannelDeleteEvent : ChannelDeleteEvent {
    override val channel: DeletedThreadChannel
    val old: ThreadChannel?
}


class TextChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: TextChannelThread?,
    override val shard: Int
) : ThreadChannelDeleteEvent {
    override fun toString(): String {
        return "TextThreadChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}


class NewsChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: NewsChannelThread?,
    override val shard: Int
) : ThreadChannelDeleteEvent {
    override fun toString(): String {
        return "NewsThreadChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}


class UnknownChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    old: ThreadChannel?,
    override val shard: Int
) : ChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelThreadDeleteEvent(channel=$channel, shard=$shard)"
    }
}
