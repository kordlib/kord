package dev.kord.core.behavior.interaction.response

import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [InteractionResponseBehavior] for an ephemeral response to an [Interaction].
 *
 * The response is only visible to the [user][Interaction.user] who invoked the interaction.
 */
public sealed interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralInteractionResponseBehavior
}
