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
data class Category(override val data: ChannelData, override val kord: Kord, override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : GuildChannel, CategoryBehavior {


    override val id: Snowflake
        get() = super.id

    override val guildId: Snowflake
        get() = super.guildId

    override val guild get() = super<GuildChannel>.guild

    override suspend fun asChannel(): Category = this

    override fun compareTo(other: Entity): Int {
        return super<GuildChannel>.compareTo(other)
    }

    /**
     * returns a new [Category] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): Category = Category(data,kord,strategy)
}

