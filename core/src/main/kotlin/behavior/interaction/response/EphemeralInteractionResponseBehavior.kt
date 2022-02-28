package dev.kord.core.behavior.interaction.response

import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The behavior of an ephemeral Discord Interaction Response
 * This response is visible to *only* to the user who made the interaction.
 */
public sealed interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralInteractionResponseBehavior
}
