package dev.kord.core.entity.interaction

import dev.kord.core.behavior.interaction.DataInteractionBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [Interaction] created when Discord requests some form of data (e.g. for auto-complete).
 *
 * @see ActionInteraction
 */
public sealed interface DataInteraction : Interaction, DataInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DataInteraction
}
