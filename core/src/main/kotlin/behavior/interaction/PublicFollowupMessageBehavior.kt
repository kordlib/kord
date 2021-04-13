package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 * This followup message is visible to all users in the channel.
 */
@KordPreview
interface PublicFollowupMessageBehavior : FollowupMessageBehavior {

    /**
     * Requests to delete this followup message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.interaction.deleteFollowupMessage(applicationId, token, id)
    }
}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [PublicFollowupMessage] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicFollowupMessageBehavior.edit(builder: PublicFollowupMessageModifyBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicFollowupMessageModifyBuilder().apply(builder)
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder.toRequest())
    return PublicFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}