package dev.kord.core.entity.channel.thread

import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A thread channel instance who's parent is a [NewsChannel].
 */
class NewsThreadChannel(
    data: ChannelData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel(data, kord, supplier) {


    override suspend fun asChannel(): NewsThreadChannel = super.asChannel() as NewsThreadChannel

    override suspend fun asChannelOrNull(): NewsThreadChannel? = super.asChannelOrNull() as? NewsThreadChannel


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsThreadChannel {
        return NewsThreadChannel(data, kord, strategy.supply(kord))
    }
}