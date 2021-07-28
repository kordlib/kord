package dev.kord.core.event.channel.thread

import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.ChannelUpdateEvent

sealed interface ThreadUpdateEvent : ChannelUpdateEvent {
    override val channel: ThreadChannel
}


class TextChannelThreadUpdateEvent(override val channel: TextChannelThread, override val shard: Int) :
    ThreadUpdateEvent {
    override fun toString(): String {
        return "TextThreadChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}


class NewsChannelThreadUpdateEvent(override val channel: NewsChannelThread, override val shard: Int) :
    ThreadUpdateEvent {
    override fun toString(): String {
        return "NewsThreadChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}


class UnknownChannelThreadUpdateEvent(override val channel: ThreadChannel, override val shard: Int) :
    ThreadUpdateEvent {
    override fun toString(): String {
        return "UnknownChannelThreadUpdateEvent(channel=$channel, shard=$shard)"
    }
}