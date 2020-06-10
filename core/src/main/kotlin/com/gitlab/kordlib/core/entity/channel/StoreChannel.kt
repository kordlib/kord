package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.StoreChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier

/**
 * An instance of a Discord Store Channel associated to a guild.
 */
data class StoreChannel(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, GuildChannel, StoreChannelBehavior {


    override suspend fun asChannel(): StoreChannel = this

    /**
     * Returns a new [StoreChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StoreChannel =
            StoreChannel(data, kord, strategy.supply(kord))
}

