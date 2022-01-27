package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.request.RestRequestException


/**
 * The behavior of a public [Discord ActionInteraction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to all users in the channel.
 */

public interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    /**
     * Requests to delete this interaction response.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

}


public fun PublicInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): PublicInteractionResponseBehavior =
    object : PublicInteractionResponseBehavior {
        override val applicationId: Snowflake = applicationId

        override val token: String = token

        override val kord: Kord = kord

        override val supplier: EntitySupplier = strategy.supply(kord)

        override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicInteractionResponseBehavior =
            PublicInteractionResponseBehavior(applicationId, token, kord, strategy)
    }
