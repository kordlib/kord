package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event

public interface ChannelCreateEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

public class CategoryCreateEvent(
    override val channel: Category,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "CategoryCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class DMChannelCreateEvent(
    override val channel: DmChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "DMChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class NewsChannelCreateEvent(
    override val channel: NewsChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "NewsChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class TextChannelCreateEvent(
    override val channel: TextChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "TextChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class VoiceChannelCreateEvent(
    override val channel: VoiceChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "VoiceChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}


public class StageChannelCreateEvent(
    override val channel: StageChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "StageChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class ForumChannelCreateEvent(
    override val channel: ForumChannel,
    override val shard : Int,
    override val customContext: Any?,

) : ChannelCreateEvent {
        override fun toString(): String {
            return "ForumChannelCreateEvent(channel=$channel, shard=$shard)"
        }
}

public class UnknownChannelCreateEvent(
    override val channel: Channel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}
