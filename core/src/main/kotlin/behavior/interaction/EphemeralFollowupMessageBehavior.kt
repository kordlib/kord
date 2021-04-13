package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.EphemeralFollowupMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 * This followup message is visible to *only* to the user who made the interaction.
 */
@KordPreview
interface EphemeralFollowupMessageBehavior : FollowupMessageBehavior

/**
 * Requests to edit this followup message.
 *
 * @return The edited [PublicFollowupMessage] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralFollowupMessageBehavior.edit(builder: EphemeralFollowupMessageModifyBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralFollowupMessageModifyBuilder().apply(builder)
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder.toRequest())
    return PublicFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}