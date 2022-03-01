package dev.kord.core.behavior.interaction.response

import dev.kord.core.entity.interaction.response.MessageInteractionResponse
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface MessageInteractionResponseBehavior : FollowupableInteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageInteractionResponseBehavior
}

/**
 * Requests to edit this [MessageInteractionResponseBehavior].
 *
 * @return The edited [MessageInteractionResponse].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun MessageInteractionResponseBehavior.edit(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): MessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editOriginalResponseWithUnknownVisibility(builder)
}
