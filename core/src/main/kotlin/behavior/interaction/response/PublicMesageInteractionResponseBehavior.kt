package dev.kord.core.behavior.interaction.response

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface PublicMessageInteractionResponseBehavior :
    PublicInteractionResponseBehavior,
    MessageInteractionResponseBehavior {

    /**
     * Requests to delete this interaction response.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PublicMessageInteractionResponseBehavior =
        PublicMessageInteractionResponseBehavior(applicationId, token, kord, strategy.supply(kord))
}

public fun PublicMessageInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): PublicMessageInteractionResponseBehavior = object : PublicMessageInteractionResponseBehavior {
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}

/**
 * Requests to edit this [PublicMessageInteractionResponseBehavior].
 *
 * @return The edited [PublicMessageInteractionResponse].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun PublicMessageInteractionResponseBehavior.edit(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): PublicMessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editPublicOriginalResponse(builder)
}
