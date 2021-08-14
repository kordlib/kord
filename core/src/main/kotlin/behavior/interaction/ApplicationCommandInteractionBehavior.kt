package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.message.create.EphemeralInteractionResponseCreateBuilder
import dev.kord.rest.builder.message.create.PublicInteractionResponseCreateBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ApplicationCommandInteractionBehavior : InteractionBehavior {

    /**
     * Acknowledges an interaction ephemerally.
     *
     * @return [EphemeralInteractionResponseBehavior] Ephemeral acknowledgement of the interaction.
     */
    suspend fun acknowledgeEphemeral(): EphemeralInteractionResponseBehavior {
        val request =  InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredChannelMessageWithSource,
            data = Optional(
                InteractionApplicationCommandCallbackData(
                    flags = Optional(MessageFlags(MessageFlag.Ephemeral))
                )
            )
        )
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return EphemeralInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges an interaction.
     *
     * @return [PublicInteractionResponseBehavior] public acknowledgement of an interaction.
     */
    suspend fun acknowledgePublic(): PublicInteractionResponseBehavior {
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredChannelMessageWithSource
        )
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return PublicInteractionResponseBehavior(applicationId, token, kord)
    }


}


/**
 * Acknowledges an interaction and responds with [PublicInteractionResponseBehavior].
 *
 * @param builder [PublicInteractionResponseCreateBuilder] used to a create an public response.
 * @return [PublicInteractionResponseBehavior] public response to the interaction.
 */

@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionBehavior.respondPublic(
    builder: PublicInteractionResponseCreateBuilder.() -> Unit
): PublicInteractionResponseBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = PublicInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return PublicInteractionResponseBehavior(applicationId, token, kord)

}


/**
 * Acknowledges an interaction and responds with [PublicInteractionResponseBehavior].
 *
 * @param builder [PublicInteractionResponseCreateBuilder] used to a create an public response.
 * @return [PublicInteractionResponseBehavior] public response to the interaction.
 */

@OptIn(ExperimentalContracts::class)
suspend inline fun ApplicationCommandInteractionBehavior.respondPublic(
    builder: PublicInteractionResponseCreateBuilder.() -> Unit
): PublicInteractionResponseBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = PublicInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return PublicInteractionResponseBehavior(applicationId, token, kord)

}


/**
 * Acknowledges an interaction and responds with [EphemeralInteractionResponseBehavior] with ephemeral flag.
 *
 * @param builder [EphemeralInteractionResponseCreateBuilder] used to a create an ephemeral response.
 * @return [EphemeralInteractionResponseBehavior] ephemeral response to the interaction.
 */

@OptIn(ExperimentalContracts::class)
suspend inline fun ApplicationCommandInteractionBehavior.respondEphemeral(
    builder: EphemeralInteractionResponseCreateBuilder.() -> Unit
): EphemeralInteractionResponseBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralInteractionResponseCreateBuilder().apply(builder)
    val request = builder.toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return EphemeralInteractionResponseBehavior(applicationId, token, kord)

}
