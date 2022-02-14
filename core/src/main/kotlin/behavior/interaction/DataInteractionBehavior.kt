package dev.kord.core.behavior.interaction

import dev.kord.core.entity.interaction.DataInteraction
import dev.kord.core.supplier.EntitySupplyStrategy

/** The behavior of a [DataInteraction]. */
public interface DataInteractionBehavior : InteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DataInteractionBehavior
}
