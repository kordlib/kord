package dev.kord.core.event.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.entity.channel.thread.DeletedThreadChannel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.event.Event

interface ChannelDeleteEvent : Event {
    val channel: Channel
    override val guildId: Snowflake?
        get() = channel.data.guildId.value
    override val kord: Kord
        get() = channel.kord
}

class CategoryDeleteEvent(override val channel: Category, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "CategoryDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class DMChannelDeleteEvent(override val channel: DmChannel, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "DMChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class NewsChannelDeleteEvent(override val channel: NewsChannel, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "NewsChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class StoreChannelDeleteEvent(override val channel: StoreChannel, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "StoreChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class TextChannelDeleteEvent(override val channel: TextChannel, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "TextChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class VoiceChannelDeleteEvent(override val channel: VoiceChannel, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "VoiceChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class StageChannelDeleteEvent(override val channel: StageChannel, override val shard: Int) : ChannelDeleteEvent {
    override fun toString(): String {
        return "StageChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

class UnknownChannelDeleteEvent(override val channel: Channel, override val shard: Int) : ChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}
