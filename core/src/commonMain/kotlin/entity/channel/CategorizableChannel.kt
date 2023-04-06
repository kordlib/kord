package dev.kord.core.entity.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.behavior.channel.CategorizableChannelBehavior
import dev.kord.core.behavior.channel.CategoryBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord channel associated to a [category].
 */
public interface CategorizableChannel : TopGuildChannel, CategorizableChannelBehavior {

    /**
     * The id of the [category] this channel belongs to, if any.
     */
    public val categoryId: Snowflake?
        get() = data.parentId.value

    /**
     * The category behavior this channel belongs to, if any.
     */
    public val category: CategoryBehavior?
        get() = when (val categoryId = categoryId) {
            null -> null
            else -> CategoryBehavior(id = categoryId, guildId = guildId, kord = kord)
        }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): CategorizableChannel
}
