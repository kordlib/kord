package dev.kord.core.event.channel.thread

import dev.kord.core.Kord
import dev.kord.core.entity.channel.thread.DeletedThreadChannel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.Event

/**
 * The event dispatched when a [ThreadChannel] is deleted in a guild.
 *
 * See [Thread Delete](https://discord.com/developers/docs/topics/gateway-events#thread-delete)
 */
public sealed interface ThreadChannelDeleteEvent : Event {
    public val channel: DeletedThreadChannel

    public val old: ThreadChannel?

    override val kord: Kord
        get() = channel.kord

}

/**
 * The event dispatched when a [TextChannelThread] is deleted in a guild.
 *
 * See [Thread Delete](https://discord.com/developers/docs/topics/gateway-events#thread-delete)
 */
public class TextChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: TextChannelThread?,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadChannelDeleteEvent {
    override fun toString(): String {
        return "TextThreadChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when a [NewsChannelThread] is deleted in a guild.
 *
 * See [Thread Delete](https://discord.com/developers/docs/topics/gateway-events#thread-delete)
 */
public class NewsChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: NewsChannelThread?,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadChannelDeleteEvent {
    override fun toString(): String {
        return "NewsThreadChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

/**
 * The event dispatched when an Unknown [ThreadChannel] is deleted in a guild.
 *
 * See [Thread Delete](https://discord.com/developers/docs/topics/gateway-events#thread-delete)
 */
public class UnknownChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: ThreadChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ThreadChannelDeleteEvent {
    override fun toString(): String {
        return "UnknownChannelThreadDeleteEvent(channel=$channel, shard=$shard)"
    }
}
