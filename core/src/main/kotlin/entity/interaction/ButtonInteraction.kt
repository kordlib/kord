package dev.kord.core.entity.interaction

import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.component.ActionRowComponent
import dev.kord.core.entity.component.ButtonComponent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public sealed interface ButtonInteraction : ComponentInteraction {
    override val component: ButtonComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<ActionRowComponent>()
            .flatMap { it.buttons }
            .firstOrNull { it.customId == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction
}

/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GlobalButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GlobalComponentInteraction, ButtonInteraction {
    override fun equals(other: Any?): Boolean {
        if (other !is ButtonInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction {
        return GlobalButtonInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GlobalButtonInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}


/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GuildButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GuildComponentInteraction, ButtonInteraction {

    override fun equals(other: Any?): Boolean {
        if (other !is ButtonInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction {
        return GuildButtonInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GuildButtonInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}
