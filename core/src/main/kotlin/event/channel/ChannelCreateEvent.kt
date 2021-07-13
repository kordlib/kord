package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.entity.channel.thread.NewsThreadChannel
import dev.kord.core.entity.channel.thread.TextThreadChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.Event

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


class StageChannelCreateEvent(override val channel: StageChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "StageChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

class TextThreadChannelCreateEvent(override val channel: TextThreadChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "TextThreadChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}


class NewsThreadChannelCreateEvent(override val channel: NewsThreadChannel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "NewsThreadChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

class UnknownChannelCreateEvent(override val channel: Channel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}