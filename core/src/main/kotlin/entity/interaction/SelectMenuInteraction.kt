package dev.kord.core.entity.interaction

import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.component.SelectMenuComponent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.component.SelectMenuBuilder

/** A [ComponentInteraction] created when a user interacts with a [select menu][SelectMenuComponent]. */
public sealed interface SelectMenuInteraction : ComponentInteraction {

    /**
     * The selected values, the expected range should between 0 and 25.
     *
     * @see SelectMenuBuilder.allowedValues
     */
    public val values: List<String> get() = data.data.values.orEmpty()

    override val component: SelectMenuComponent
        get() = message.actionRows.firstNotNullOf { it.selectMenus[componentId] }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction
}

/** A [SelectMenuInteraction] that took place in the context of a [Guild]. */
public class GuildSelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : SelectMenuInteraction, GuildComponentInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildSelectMenuInteraction =
        GuildSelectMenuInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildSelectMenuInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildSelectMenuInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [SelectMenuInteraction] that took place in a global context (e.g. a DM). */
public class GlobalSelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier,
) : SelectMenuInteraction, GlobalComponentInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalSelectMenuInteraction =
        GlobalSelectMenuInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalSelectMenuInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalSelectMenuInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
