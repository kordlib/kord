package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.NewsChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier

/**
 * An instance of a Discord News Channel associated to a guild.
 */
data class NewsChannel(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, GuildMessageChannel, NewsChannelBehavior {

    /**
     * Returns a new [NewsChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannel =
            NewsChannel(data, kord, strategy.supply(kord))
}

