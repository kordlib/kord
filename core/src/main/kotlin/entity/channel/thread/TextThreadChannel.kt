package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ChannelType
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class TextThreadChannel(
    data: ChannelData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel(data, kord, supplier) {
    val isPrivate get() = data.type == ChannelType.PrivateThread


    override suspend fun asChannel(): TextThreadChannel = super.asChannel() as TextThreadChannel

    override suspend fun asChannelOrNull(): TextThreadChannel? = super.asChannelOrNull() as? TextThreadChannel

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannel {
     return TextThreadChannel(data, kord, strategy.supply(kord))
    }

}