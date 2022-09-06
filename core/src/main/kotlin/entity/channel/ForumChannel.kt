package dev.kord.core.entity.channel

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder

public class ForumChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadParentChannel, ThreadParentChannelBehavior {

    /**
     * The channel topic, if present.
     */
    public val topic: String? get() = data.topic.value

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannel {
        return ForumChannel(data,kord, strategy.supply(kord))
    }

}