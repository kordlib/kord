package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ChannelType
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A thread channel instance whose parent is a [TextChannel].
 */
class TextChannelThread(
    data: ChannelData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel(data, kord, supplier) {
    val isPrivate get() = data.type == ChannelType.PrivateThread


    override suspend fun asChannel(): TextChannelThread = super.asChannel() as TextChannelThread

    override suspend fun asChannelOrNull(): TextChannelThread? = super.asChannelOrNull() as? TextChannelThread

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelThread {
        return TextChannelThread(data, kord, strategy.supply(kord))
    }

}