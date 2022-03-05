package dev.kord.core.behavior.interaction.response

import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [InteractionResponseBehavior] for a public response to an [Interaction].
 *
 * The response is visible to all users in the [channel][InteractionBehavior.channel] the interaction was sent from.
 */
public sealed interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicInteractionResponseBehavior
}
