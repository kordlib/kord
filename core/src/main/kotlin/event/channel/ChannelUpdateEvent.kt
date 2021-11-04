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
    public val old: Category?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "CategoryUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class DMChannelUpdateEvent(
    override val channel: DmChannel,
    public val old: DmChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "DMChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class NewsChannelUpdateEvent(
    override val channel: NewsChannel,
    public val old: NewsChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class StoreChannelUpdateEvent(
    override val channel: StoreChannel,
    public val old: StoreChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StoreChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class TextChannelUpdateEvent(
    override val channel: TextChannel,
    public val old: TextChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class VoiceChannelUpdateEvent(
    override val channel: VoiceChannel,
    public val old: VoiceChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope{
    override fun toString(): String {
        return "VoiceChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}


public class StageChannelUpdateEvent(
    override val channel: StageChannel,
    public val old: StageChannel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StageChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class UnknownChannelUpdateEvent(
    override val channel: Channel,
    public val old: Channel?,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelUpdateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}
