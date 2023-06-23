package dev.kord.core.behavior.interaction

import dev.kord.core.entity.interaction.GlobalInteraction
import dev.kord.core.supplier.EntitySupplyStrategy

/** The behavior of a [GlobalInteraction]. */
public interface GlobalInteractionBehavior : InteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalInteractionBehavior
}
