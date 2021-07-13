package dev.kord.core.entity.channel.thread

import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

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