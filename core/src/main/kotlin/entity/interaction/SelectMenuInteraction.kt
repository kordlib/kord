package dev.kord.core.entity.interaction

import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.component.ActionRowComponent
import dev.kord.core.entity.component.ButtonComponent
import dev.kord.core.entity.component.SelectMenuComponent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.component.SelectMenuBuilder

/**
 * An interaction created from a user interacting with a [SelectMenuComponent].
 */

public sealed interface SelectMenuInteraction : ComponentInteraction {

    /**
     * The selected values, the expected range should between 0 and 25.
     *
     * @see [SelectMenuBuilder.minimumValues]
     * @see [SelectMenuBuilder.maximumValues]
     */
    public val values: List<String> get() = data.data.values.orEmpty()

    override val component: SelectMenuComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<ActionRowComponent>()
            .flatMap { it.selectMenus }
            .firstOrNull { it.customId == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction

}

/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GuildSelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GuildComponentInteraction, SelectMenuInteraction {
    override fun equals(other: Any?): Boolean {
        if (other !is SelectMenuInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction {
        return GuildSelectMenuInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GuildSelectMenuInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}


/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GlobalSelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GlobalComponentInteraction, SelectMenuInteraction {
    override fun equals(other: Any?): Boolean {
        if (other !is SelectMenuInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction {
        return GlobalSelectMenuInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GlobalSelectMenuInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}
