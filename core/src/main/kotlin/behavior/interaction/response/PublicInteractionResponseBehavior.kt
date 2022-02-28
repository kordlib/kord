package dev.kord.core.behavior.interaction.response

import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The behavior of a public Discord Interaction Response.
 * This response is visible to all users in the channel.
 */
public sealed interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicInteractionResponseBehavior
}
