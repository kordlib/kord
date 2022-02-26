package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.request.RestRequestException

public interface PublicMessageInteractionResponseBehavior : PublicInteractionResponseBehavior,
    MessageInteractionResponseBehavior {

    /**
     * Requests to delete this interaction response.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

    public override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicMessageInteractionResponseBehavior {
        return PublicMessageInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
    }
}

public fun PublicMessageInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): PublicMessageInteractionResponseBehavior =
    object : PublicMessageInteractionResponseBehavior {
        override val applicationId: Snowflake = applicationId

        override val token: String = token

        override val kord: Kord = kord

        override val supplier: EntitySupplier = supplier
    }
