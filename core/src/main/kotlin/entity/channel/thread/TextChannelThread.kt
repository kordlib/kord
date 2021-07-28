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
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel {
    val isPrivate get() = data.type == ChannelType.PrivateThread


    override suspend fun asChannel(): TextChannelThread = super.asChannel() as TextChannelThread

    override suspend fun asChannelOrNull(): TextChannelThread? = super.asChannelOrNull() as? TextChannelThread

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelThread {
        return TextChannelThread(data, kord, strategy.supply(kord))
    }

}