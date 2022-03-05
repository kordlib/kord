package dev.kord.core.behavior.interaction.response

import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.entity.interaction.response.MessageInteractionResponse
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An [InteractionResponseBehavior] returned when using [ActionInteractionBehavior.deferPublicResponse] or
 * [ActionInteractionBehavior.deferEphemeralResponse].
 *
 * The main operation this handle supports is [respond][DeferredMessageInteractionResponseBehavior.respond], the user
 * will see a 'loading' animation until it is invoked.
 *
 * This handle does not support sending followup messages to the interaction.
 */
public sealed interface DeferredMessageInteractionResponseBehavior : InteractionResponseBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DeferredMessageInteractionResponseBehavior
}

/**
 * Sends a response message that was previously deferred by using [ActionInteractionBehavior.deferPublicResponse] or
 * [ActionInteractionBehavior.deferEphemeralResponse].
 *
 * This function is supposed to be only invoked once, use the returned [MessageInteractionResponse] for more operations.
 *
 * @param builder [InteractionResponseModifyBuilder] used to create the response message.
 * @return [MessageInteractionResponse] the response message to the interaction.
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun DeferredMessageInteractionResponseBehavior.respond(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): MessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    return editOriginalResponseWithUnknownVisibility(builder)
}
