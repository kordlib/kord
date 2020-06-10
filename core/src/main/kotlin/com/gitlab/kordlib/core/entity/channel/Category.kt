package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.CategoryBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier

/**
 * An instance of a Discord category associated to a [guild].
 */
class Category(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildChannel, CategoryBehavior {

    /**
     * Returns a new [Category] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Category =
            Category(data, kord, strategy.supply(kord))
}
