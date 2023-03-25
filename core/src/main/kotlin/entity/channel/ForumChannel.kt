package dev.kord.core.entity.channel

import dev.kord.common.entity.*
import dev.kord.common.entity.Permission.ManageChannels
import dev.kord.common.entity.Permission.ManageMessages
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ForumChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlin.time.Duration

public class ForumChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadParentChannel, ForumChannelBehavior {

    /**
     * The channel topic, if present.
     */
    public val topic: String? get() = data.topic.value

    /** Whether the channel is nsfw. */
    public val isNsfw: Boolean get() = data.nsfw.orElse(false)

    public val availableTags: List<ForumTag> get() = data.availableTags.value ?: emptyList()

    public val defaultReactionEmoji: DefaultReaction? get() = data.defaultReactionEmoji.value

    /**
     * The amount of time a user has to wait before creating another thread.
     *
     * Bots, as well as users with the permission [ManageMessages] or [ManageChannels], are unaffected.
     */
    public val rateLimitPerUser: Duration? get() = data.rateLimitPerUser.value

    public val defaultThreadRateLimitPerUser: Duration? get() = data.defaultThreadRateLimitPerUser.value

    public val defaultAutoArchiveDuration: ArchiveDuration? get() = data.defaultAutoArchiveDuration.value

    /** The default sort order type used to order posts in this forum channel. */
    public val defaultSortOrder: SortOrderType? get() = data.defaultSortOrder.value

    /**
     * The default layout of the forum, if present.
     */
    public val defaultForumLayout: ForumLayoutType? get() = data.defaultForumLayout.value

    override suspend fun asChannel(): ForumChannel = this
    override suspend fun asChannelOrNull(): ForumChannel = this
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannel =
        ForumChannel(data, kord, strategy.supply(kord))

    override fun toString(): String = "ForumChannel(data=$data, kord=$kord, supplier=$supplier)"
}
