package dev.kord.core.entity.channel

import dev.kord.common.entity.DiscordDefaultReaction
import dev.kord.common.entity.DiscordForumTag
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ForumChannelBehavior
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlin.time.Duration

public class ForumChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadParentChannel, ThreadParentChannelBehavior, ForumChannelBehavior {

    /**
     * The channel topic, if present.
     */
    public val topic: String? get() = data.topic.value

    public val availableTags: List<DiscordForumTag>? get() = data.availableTags.value

    public val defaultReactionEmoji: DiscordDefaultReaction? get() = data.defaultReactionEmoji.value

    public val defaultThreadRateLimitPerUser: Duration? get() = data.defaultThreadRateLimitPerUser.value

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannel {
        return ForumChannel(data,kord, strategy.supply(kord))
    }

}