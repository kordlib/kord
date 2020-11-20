package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.Event

interface ChannelCreateEvent : Event {
    val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

class CategoryCreateEvent(override val channel: Category, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "CategoryCreateEvent(channel=$channel, shard=$shard)"
    }
}

class DMChannelCreateEvent(override val channel: DmChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "DMChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

class NewsChannelCreateEvent(override val channel: NewsChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "NewsChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

class StoreChannelCreateEvent(override val channel: StoreChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "StoreChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

class TextChannelCreateEvent(override val channel: TextChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "TextChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

class VoiceChannelCreateEvent(override val channel: VoiceChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "VoiceChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}
