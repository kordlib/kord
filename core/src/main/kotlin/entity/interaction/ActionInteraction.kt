package dev.kord.core.entity.interaction

import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [Interaction] created when a user performs some form of action (e.g. using a slash command or pressing a button).
 *
 * @see DataInteraction
 */
public sealed interface ActionInteraction : Interaction, ActionInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ActionInteraction
}
