package dev.kord.core.entity.channel.thread

import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A thread channel instance who's parent is a [NewsChannel].
 */
class NewsChannelThread(
    data: ChannelData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel(data, kord, supplier) {


    override suspend fun asChannel(): NewsChannelThread = super.asChannel() as NewsChannelThread

    override suspend fun asChannelOrNull(): NewsChannelThread? = super.asChannelOrNull() as? NewsChannelThread


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannelThread {
        return NewsChannelThread(data, kord, strategy.supply(kord))
    }
}