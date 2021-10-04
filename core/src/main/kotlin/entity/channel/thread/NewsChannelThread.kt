package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * A thread channel instance whose parent is a [NewsChannel].
 */
public class NewsChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel {


    override suspend fun asChannel(): NewsChannelThread = this

    override suspend fun asChannelOrNull(): NewsChannelThread? = this


    override suspend fun getParent(): NewsChannel {
        return supplier.getChannelOf(parentId)
    }

    override suspend fun getParentOrNull(): NewsChannel? {
        return supplier.getChannelOfOrNull(parentId)
    }

    override val guildId: Snowflake
        get() = data.guildId.value!!


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannelThread {
        return NewsChannelThread(data, kord, strategy.supply(kord))
    }
}
