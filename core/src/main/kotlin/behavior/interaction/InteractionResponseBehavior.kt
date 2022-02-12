package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.FollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord ActionInteraction Response](https://discord.com/developers/docs/interactions/receiving-and-responding#responding-to-an-interaction)
 */
public interface InteractionResponseBehavior : KordObject, Strategizable {
    public val applicationId: Snowflake
    public val token: String

    /**
     * Returns a followup message for an interaction response or `null` if it was not found.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun getFollowupMessageOrNull(messageId: Snowflake): FollowupMessage? =
        supplier.getFollowupMessageOrNull(applicationId, token, messageId)

    /**
     * Returns a followup message for an interaction response.
     *
     * @throws RestRequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the followup message was not found.
     */
    public suspend fun getFollowupMessage(messageId: Snowflake): FollowupMessage =
        supplier.getFollowupMessage(applicationId, token, messageId)
}

@Deprecated(
    "Renamed to 'followUpPublic'",
    ReplaceWith("this.followUpPublic { builder() }", "dev.kord.core.behavior.interaction.followUpPublic"),
)
public suspend inline fun InteractionResponseBehavior.followUp(builder: FollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return followUpPublic(builder)
}

/**
 * Follows up an interaction response without the [Ephemeral flag][dev.kord.common.entity.MessageFlag.Ephemeral].
 */
public suspend inline fun InteractionResponseBehavior.followUpPublic(builder: FollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral = false, builder)
    return PublicFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}

/**
 * Follows up an interaction response with the [Ephemeral flag][dev.kord.common.entity.MessageFlag.Ephemeral].
 */
public suspend inline fun InteractionResponseBehavior.followUpEphemeral(builder: FollowupMessageCreateBuilder.() -> Unit): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, ephemeral = true, builder)
    return EphemeralFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}

/**
 * Requests to edit this interaction response.
 *
 * @return The edited [Message] of the interaction response.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun InteractionResponseBehavior.edit(
    builder: InteractionResponseModifyBuilder.() -> Unit,
): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val message = kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder)
    return Message(message.toData(), kord)
}
