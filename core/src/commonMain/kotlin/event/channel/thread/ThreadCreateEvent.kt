package dev.kord.core.event.channel.thread

import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.ChannelCreateEvent

/**
 * The event dispatched when a [ThreadChannel] is created in a guild.
 *
 * See [Thread Create](https://discord.com/developers/docs/topics/gateway-events#thread-create)
 */
public sealed interface ThreadChannelCreateEvent : ChannelCreateEvent {
    override val channel: ThreadChannel
}

/**
 * The event dispatched when a [TextChannelThread] is created in a guild.
 *
 * See [Thread Create](https://discord.com/developers/docs/topics/gateway-events#thread-create)
 */
public class TextChannelThreadCreateEvent(
    override val channel: TextChannelThread,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadChannelCreateEvent {
    override fun toString(): String {
        return "TextThreadChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [NewsChannelThread] is created in a guild.
 *
 * See [Thread Create](https://discord.com/developers/docs/topics/gateway-events#thread-create)
 */
public class NewsChannelThreadCreateEvent(
    override val channel: NewsChannelThread,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadChannelCreateEvent {
    override fun toString(): String {
        return "NewsThreadChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when an Unknown [ThreadChannel] is created in a guild.
 *
 * See [Thread Create](https://discord.com/developers/docs/topics/gateway-events#thread-create)
 */
public class UnknownChannelThreadCreateEvent(
    override val channel: ThreadChannel,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadChannelCreateEvent {
    override fun toString(): String {
        return "UnknownChannelThreadCreateEvent(channel=$channel, shard=$shard)"
    }
}
