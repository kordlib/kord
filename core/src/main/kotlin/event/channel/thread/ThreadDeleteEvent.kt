package dev.kord.core.event.channel.thread

import dev.kord.core.Kord
import dev.kord.core.entity.channel.thread.DeletedThreadChannel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public sealed interface ThreadChannelDeleteEvent : Event {
    public val channel: DeletedThreadChannel

    public val old: ThreadChannel?

    override val kord: Kord
        get() = channel.kord

}


public class TextChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: TextChannelThread?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextThreadChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}


public class NewsChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: NewsChannelThread?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsThreadChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}


public class UnknownChannelThreadDeleteEvent(
    override val channel: DeletedThreadChannel,
    override val old: ThreadChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ThreadChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelThreadDeleteEvent(channel=$channel, shard=$shard)"
    }
}
