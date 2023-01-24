package dev.kord.core.entity.channel

import dev.kord.common.entity.ForumLayoutType
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class ForumChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadParentChannel, ThreadParentChannelBehavior {

    /**
     * The channel topic, if present.
     */
    public val topic: String? get() = data.topic.value

    /**
     * The default layout of the forum, if present.
     */
    public val defaultForumLayout: ForumLayoutType? get() = data.defaultForumLayout.value

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannel {
        return ForumChannel(data,kord, strategy.supply(kord))
    }

}