package dev.kord.core.entity.channel

import dev.kord.common.entity.DiscordDefaultReaction
import dev.kord.common.entity.DiscordForumTag
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.ForumLayoutType
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ForumChannelBehavior
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.ForumTagBuilder
import dev.kord.rest.builder.channel.ModifyForumTagBuilder
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

    public suspend fun createTag(name: String, builder: ForumTagBuilder.() -> Unit = {}): ForumChannel {
        val request = kord.rest.channel.createForumTag(data.id, name, builder)
        val data = ChannelData.from(request)

        return Channel.from(data, kord) as ForumChannel
    }

    public suspend fun deleteTag(tagId: Snowflake, reason: String? = null): ForumChannel {
        val request = kord.rest.channel.deleteForumTag(data.id, tagId, reason)
        val data = ChannelData.from(request)

        return Channel.from(data, kord) as ForumChannel
    }

    public suspend fun editTag(tagId: Snowflake, builder: ModifyForumTagBuilder.() -> Unit): ForumChannel {
        val request = kord.rest.channel.editForumTag(data.id, tagId, builder)
        val data = ChannelData.from(request)

        return Channel.from(data, kord) as ForumChannel
    }

    /**
     * The default layout of the forum, if present.
     */
    public val defaultForumLayout: ForumLayoutType? get() = data.defaultForumLayout.value

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannel {
        return ForumChannel(data,kord, strategy.supply(kord))
    }

    override fun toString(): String {
        return "ForumChannel(data=$data, kord=$kord, supplier=$supplier)"
    }
}