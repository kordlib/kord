package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.message.create.EphemeralFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.modify.EphemeralInteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a ephemeral [Discord Interaction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to *only* to the user who made the interaction.
 */

interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior

/**
 * Requests to edit this interaction response.
 *
 * @return The edited [Message] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */

@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralInteractionResponseBehavior.edit(builder: EphemeralInteractionResponseModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralInteractionResponseModifyBuilder().apply(builder)
    kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder.toRequest())
}

/**
 * Follows-up this interaction response with a [EphemeralFollowupMessage]
 *
 * @return created [EphemeralFollowupMessage]
 */
@OptIn(ExperimentalContracts::class)

suspend inline fun EphemeralInteractionResponseBehavior.followUpEphemeral(
    builder: EphemeralFollowupMessageCreateBuilder.() -> Unit
): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralFollowupMessageCreateBuilder().apply(builder)
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    val message = Message(response.toData(), kord)
    return EphemeralFollowupMessage(message, applicationId, token, kord)
}


/**
 * Follows-up this interaction response with a [PublicFollowupMessage]
 *
 * This function assumes that this interaction response has content in it.
 * Use [the safe method overload][EphemeralInteractionResponseBehavior.followUp] if you are unsure
 *
 * @return created [PublicFollowupMessage]
 */
@OptIn(ExperimentalContracts::class)

@KordUnsafe
suspend inline fun EphemeralInteractionResponseBehavior.followUpPublic(
    builder: PublicFollowupMessageCreateBuilder.() -> Unit
): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicFollowupMessageCreateBuilder().apply(builder)
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    val message = Message(response.toData(), kord)
    return PublicFollowupMessage(message, applicationId, token, kord)
}


fun EphemeralInteractionResponseBehavior(
    applicationId: Snowflake,
    token: String,
    kord: Kord
): EphemeralInteractionResponseBehavior =
    object : EphemeralInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }
