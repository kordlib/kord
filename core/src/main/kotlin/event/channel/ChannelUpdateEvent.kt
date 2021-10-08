package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public interface ChannelUpdateEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

public class CategoryUpdateEvent(
    override val channel: Category,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "CategoryUpdateEvent(channel=$channel, shard=$shard)"
    }
}

public class DMChannelUpdateEvent(
    override val channel: DmChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "DMChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}

public class NewsChannelUpdateEvent(
    override val channel: NewsChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}

public class StoreChannelUpdateEvent(
    override val channel: StoreChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StoreChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}

public class TextChannelUpdateEvent(
    override val channel: TextChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}

public class VoiceChannelUpdateEvent(
    override val channel: VoiceChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope{
    override fun toString(): String {
        return "VoiceChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}


public class StageChannelUpdateEvent(
    override val channel: StageChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StageChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}

public class UnknownChannelUpdateEvent(
    override val channel: Channel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelUpdateEvent(channel=$channel, shard=$shard)"
    }
}
