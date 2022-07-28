package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.DeprecationLevel.ERROR

public interface ChannelDeleteEvent : Event {
    public val channel: Channel
    override val kord: Kord
        get() = channel.kord
}

public class CategoryDeleteEvent(
    override val channel: Category,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "CategoryDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class DMChannelDeleteEvent(
    override val channel: DmChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "DMChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class NewsChannelDeleteEvent(
    override val channel: NewsChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "NewsChannelDeleteEvent(channel=$channel, shard=$shard)"
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
public class StoreChannelDeleteEvent(
    override val channel: StoreChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StoreChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class TextChannelDeleteEvent(
    override val channel: TextChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "TextChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class VoiceChannelDeleteEvent(
    override val channel: VoiceChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "VoiceChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class StageChannelDeleteEvent(
    override val channel: StageChannel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "StageChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}

public class UnknownChannelDeleteEvent(
    override val channel: Channel,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(channel.kord)

) : ChannelDeleteEvent, CoroutineScope by coroutineScope {
    override fun toString(): String {
        return "UnknownChannelDeleteEvent(channel=$channel, shard=$shard)"
    }
}
