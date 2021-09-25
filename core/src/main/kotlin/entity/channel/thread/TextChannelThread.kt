package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * A thread channel instance whose parent is a [TextChannel].
 */
class TextChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel {
    /**
     * Whether this thread is private
     */
    val isPrivate get() = data.type == ChannelType.PrivateThread

    override suspend fun getParent(): TextChannel {
        return supplier.getChannelOf(parentId)
    }

    override suspend fun getParentOrNull(): TextChannel? {
        return supplier.getChannelOfOrNull(parentId)
    }

    override val guildId: Snowflake
        get() = data.guildId.value!!

    override suspend fun asChannel(): TextChannelThread = this

    override suspend fun asChannelOrNull(): TextChannelThread = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelThread {
        return TextChannelThread(data, kord, strategy.supply(kord))
    }

}