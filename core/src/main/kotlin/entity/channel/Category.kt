package dev.kord.core.entity.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.CategoryBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.Entity
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord category associated to a [guild].
 */
public class Category(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : TopGuildChannel, CategoryBehavior {

    override val guildId: Snowflake
        get() = super.guildId

    override val guild: GuildBehavior get() = super<TopGuildChannel>.guild

    override suspend fun asChannel(): Category = this

    override suspend fun asChannelOrNull(): Category = this

    override fun compareTo(other: Entity): Int {
        return super<TopGuildChannel>.compareTo(other)
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
