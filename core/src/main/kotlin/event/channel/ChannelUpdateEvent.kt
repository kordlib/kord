package dev.kord.core.event.channel

import dev.kord.core.Kord
import dev.kord.core.entity.channel.*
import dev.kord.core.event.Event
import kotlin.DeprecationLevel.HIDDEN


public interface ChannelUpdateEvent : Event {
    public val channel: Channel
    public val old: Channel?
    override val kord: Kord
        get() = channel.kord
}

public class CategoryUpdateEvent(
    override val channel: Category,
    override val old: Category?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "CategoryUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class DMChannelUpdateEvent(
    override val channel: DmChannel,
    override val old: DmChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "DMChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class NewsChannelUpdateEvent(
    override val channel: NewsChannel,
    override val old: NewsChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "NewsChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

/** @suppress */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/6309018858647-Self-serve-Game-Selling-Deprecation for more
    information.
    """,
    level = HIDDEN,
)
public class StoreChannelUpdateEvent(
    override val channel: dev.kord.core.entity.channel.StoreChannel,
    override val old: dev.kord.core.entity.channel.StoreChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "StoreChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class TextChannelUpdateEvent(
    override val channel: TextChannel,
    override val old: TextChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "TextChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class VoiceChannelUpdateEvent(
    override val channel: VoiceChannel,
    override val old: VoiceChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "VoiceChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}


public class StageChannelUpdateEvent(
    override val channel: StageChannel,
    override val old: StageChannel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "StageChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}

public class UnknownChannelUpdateEvent(
    override val channel: Channel,
    override val old: Channel?,
    override val shard: Int,
    override val customContext: Any?,
) : ChannelUpdateEvent {
    override fun toString(): String {
        return "UnknownChannelUpdateEvent(channel=$channel, old=$old, shard=$shard)"
    }
}
