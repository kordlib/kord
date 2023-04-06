package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event

/**
 * The event dispatched when a [Channel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public interface ChannelDeleteEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

/**
 * The event dispatched when a [Category] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class CategoryDeleteEvent(
    override val channel: Category,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "CategoryDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [DmChannel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class DMChannelDeleteEvent(
    override val channel: DmChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "DMChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [NewsChannel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class NewsChannelDeleteEvent(
    override val channel: NewsChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "NewsChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [TextChannel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class TextChannelDeleteEvent(
    override val channel: TextChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "TextChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [VoiceChannel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class VoiceChannelDeleteEvent(
    override val channel: VoiceChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "VoiceChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [StageChannel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class StageChannelDeleteEvent(
    override val channel: StageChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "StageChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class ForumChannelDeleteEvent(
    override val channel: ForumChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "ForumChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}


/**
 * The event dispatched when an Unknown [Channel] is deleted in a guild.
 *
 * See [Channel Delete](https://discord.com/developers/docs/topics/gateway-events#channel-delete)
 */
public class UnknownChannelDeleteEvent(
    override val channel: Channel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelDeleteEvent {
    override fun toString(): String {
        return "UnknownChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}
