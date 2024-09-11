package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event

/**
 * The event dispatched when a [Channel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public interface ChannelCreateEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

/**
 * The event dispatched when a [Category] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class CategoryCreateEvent(
    override val channel: Category,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "CategoryCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [DmChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class DMChannelCreateEvent(
    override val channel: DmChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "DMChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [NewsChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class NewsChannelCreateEvent(
    override val channel: NewsChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "NewsChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [TextChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class TextChannelCreateEvent(
    override val channel: TextChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "TextChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [VoiceChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class VoiceChannelCreateEvent(
    override val channel: VoiceChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "VoiceChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [StageChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class StageChannelCreateEvent(
    override val channel: StageChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "StageChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [ForumChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class ForumChannelCreateEvent(
    override val channel: ForumChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "ForumChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [MediaChannel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class MediaChannelCreateEvent(
    override val channel: MediaChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String =
        "MediaChannelCreateEvent(channel=$channel, shard=$shard, customContext=$customContext)"
}

/**
 * The event dispatched when an Unknown [Channel] is created in a guild.
 *
 * See [Channel Create](https://discord.com/developers/docs/topics/gateway-events#channel-create)
 */
public class UnknownChannelCreateEvent(
    override val channel: Channel,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}
