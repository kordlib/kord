package dev.kord.core.behavior.interaction.response

import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed interface  MessageInteractionResponseBehavior : InteractionResponseBehavior
/**
 * Requests to edit this interaction response.
 *
 * @return The edited [Message] of the interaction response.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun MessageInteractionResponseBehavior.edit(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val message = kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder)
    return Message(message.toData(), kord)
}

