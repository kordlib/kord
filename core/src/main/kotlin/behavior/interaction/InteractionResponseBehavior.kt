package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
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

interface InteractionResponseBehavior : KordObject {
    val applicationId: Snowflake
    val token: String

}

@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionResponseBehavior.followUp(ephemeral: Boolean = false, builder: FollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = FollowupMessageCreateBuilder(ephemeral).apply(builder)
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    return PublicFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}


/**
 * Requests to edit this interaction response.
 *
 * @return The edited [Message] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */

@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionResponseBehavior.edit(builder: InteractionResponseModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = InteractionResponseModifyBuilder().apply(builder)
    kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder.toRequest())
}
