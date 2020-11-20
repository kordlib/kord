package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.CategoryBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord category associated to a [guild].
 */
class Category(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier,
) : GuildChannel, CategoryBehavior {

    override val guildId: Snowflake
        get() = super.guildId

    override val guild get() = super<GuildChannel>.guild

    override suspend fun asChannel(): Category = this

    override fun compareTo(other: Entity): Int {
        return super<GuildChannel>.compareTo(other)
    }


    /**
     * Returns a new [Category] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Category =
            Category(data, kord, strategy.supply(kord))


    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Category(data=$data, kord=$kord, supplier=$supplier)"
    }

}