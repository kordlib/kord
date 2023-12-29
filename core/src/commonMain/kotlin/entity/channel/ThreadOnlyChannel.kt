package dev.kord.core.entity.channel

import dev.kord.common.entity.*
import dev.kord.common.entity.Permission.ManageChannels
import dev.kord.common.entity.Permission.ManageMessages
import dev.kord.core.behavior.channel.threads.ThreadOnlyChannelBehavior
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlin.time.Duration

public sealed interface ThreadOnlyChannel : ThreadParentChannel, ThreadOnlyChannelBehavior {

    /** The channel topic, if present. */
    public val topic: String? get() = data.topic.value

    /** Whether the channel is nsfw. */
    public val isNsfw: Boolean get() = data.nsfw.orElse(false)

    /** The id of the last thread created in this channel (may not point to an existing or valid thread). */
    public val lastThreadId: Snowflake? get() = data.lastMessageId?.value

    /**
     * The amount of time a user has to wait before creating another thread.
     *
     * Bots, as well as users with the permission [ManageMessages] or [ManageChannels], are unaffected.
     */
    public val rateLimitPerUser: Duration? get() = data.rateLimitPerUser.value

    /**
     * Default [ArchiveDuration], copied onto newly created threads in this channel. Threads will stop showing in the
     * channel list after the specified period of inactivity.
     */
    public val defaultAutoArchiveDuration: ArchiveDuration? get() = data.defaultAutoArchiveDuration.value

    /** The set of tags that can be used in this channel. */
    public val availableTags: List<ForumTag> get() = data.availableTags.value ?: emptyList()

    /** The emoji to show in the add reaction button on a thread in this channel. */
    public val defaultReactionEmoji: DefaultReaction? get() = data.defaultReactionEmoji.value

    /**
     * The initial [ThreadChannel.rateLimitPerUser] to set on newly created threads in this channel. This field is
     * copied to the thread at creation time and does not live update.
     */
    public val defaultThreadRateLimitPerUser: Duration? get() = data.defaultThreadRateLimitPerUser.value

    /** The default [SortOrderType] used to order posts in this channel. */
    public val defaultSortOrder: SortOrderType? get() = data.defaultSortOrder.value

    override suspend fun asChannel(): ThreadOnlyChannel
    override suspend fun asChannelOrNull(): ThreadOnlyChannel
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadOnlyChannel

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}
