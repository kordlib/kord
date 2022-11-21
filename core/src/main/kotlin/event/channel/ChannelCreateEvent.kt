package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event

/**
 * The event dispatched when a [Channel] is created in a guild.
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
 */
public interface ChannelCreateEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

/**
 * The event dispatched when a [Category] is created in a guild.
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
 * The event dispatched when an Unknown [Channel] is created in a guild.
 *
 * @see <a href="https://discord.com/developers/docs/topics/gateway#channel-create">Channel Create</a>
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
