package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.DeprecationLevel.ERROR

public interface ChannelCreateEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

public class CategoryCreateEvent(
    override val channel: Category,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "CategoryCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class DMChannelCreateEvent(
    override val channel: DmChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "DMChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class NewsChannelCreateEvent(
    override val channel: NewsChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

/** @suppress */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = ERROR,
)
public class StoreChannelCreateEvent(
    override val channel: StoreChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StoreChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class TextChannelCreateEvent(
    override val channel: TextChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class VoiceChannelCreateEvent(
    override val channel: VoiceChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "VoiceChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}


public class StageChannelCreateEvent(
    override val channel: StageChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StageChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}

public class UnknownChannelCreateEvent(
    override val channel: Channel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)
) : ChannelCreateEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelCreateEvent(channel=$channel, shard=$shard)"
    }
}
