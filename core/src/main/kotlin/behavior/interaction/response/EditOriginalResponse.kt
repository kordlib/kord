package dev.kord.core.behavior.interaction.response

import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.response.EphemeralMessageInteractionResponse
import dev.kord.core.entity.interaction.response.MessageInteractionResponse
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// internal utility functions to avoid duplications in DeferredMessageInteractionResponseBehavior.respond() and
// MessageInteractionResponseBehavior.edit() since they are using the same 'Edit Original Interaction Response' endpoint
// https://discord.com/developers/docs/interactions/receiving-and-responding#edit-original-interaction-response


@PublishedApi
internal suspend inline fun InteractionResponseBehavior.editOriginalResponse(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder)
    return Message(response.toData(), kord)
}


@PublishedApi
internal suspend inline fun InteractionResponseBehavior.editOriginalResponseWithUnknownVisibility(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): MessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val public = when (this) {
        is PublicInteractionResponseBehavior -> true
        is EphemeralInteractionResponseBehavior -> false
        else -> error(
            "This function can't be called on an InteractionResponseBehavior that implements neither " +
                    "PublicInteractionResponseBehavior nor EphemeralInteractionResponseBehavior."
        )
    }

    val message = editOriginalResponse(builder)

    return when {
        public -> PublicMessageInteractionResponse(message, applicationId, token, kord)
        else -> EphemeralMessageInteractionResponse(message, applicationId, token, kord)
    }
}


@PublishedApi
internal suspend inline fun PublicInteractionResponseBehavior.editPublicOriginalResponse(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): PublicMessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val message = editOriginalResponse(builder)
    return PublicMessageInteractionResponse(message, applicationId, token, kord)
}


@PublishedApi
internal suspend inline fun EphemeralInteractionResponseBehavior.editEphemeralOriginalResponse(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): EphemeralMessageInteractionResponse {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val message = editOriginalResponse(builder)
    return EphemeralMessageInteractionResponse(message, applicationId, token, kord)
}
