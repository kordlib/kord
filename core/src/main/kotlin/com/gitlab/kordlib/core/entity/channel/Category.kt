package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.CategoryBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Entity

/**
 * An instance of a Discord category associated to a [guild].
 */
class Category(
        override val data: ChannelData,
        override val kord: Kord,
        override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : GuildChannel, CategoryBehavior {

    /**
     * Returns a new [Category] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): Category = Category(data, kord, strategy)
}
