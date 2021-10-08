package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Interaction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 */

public interface InteractionResponseBehavior : KordObject {
    public val applicationId: Snowflake
    public val token: String

}

@OptIn(ExperimentalContracts::class)
public suspend inline fun InteractionResponseBehavior.followUp(builder: FollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = FollowupMessageCreateBuilder(false).apply(builder)
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    return PublicFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}


@OptIn(ExperimentalContracts::class)
public suspend inline fun InteractionResponseBehavior.ephemeralFollowup(builder: FollowupMessageCreateBuilder.() -> Unit): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = FollowupMessageCreateBuilder(false).apply(builder)
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    return EphemeralFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}

/**
 * Requests to edit this interaction response.
 *
 * @return The edited [Message] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */

@OptIn(ExperimentalContracts::class)
public suspend inline fun InteractionResponseBehavior.edit(builder: InteractionResponseModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = InteractionResponseModifyBuilder().apply(builder)
    kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder.toRequest())
}
