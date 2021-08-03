package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.message.create.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.modify.PublicInteractionResponseModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * The behavior of a public [Discord Interaction Response](https://discord.com/developers/docs/interactions/slash-commands#interaction-response)
 * This response is visible to all users in the channel.
 */
@KordPreview
interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    /**
     * Requests to delete this interaction response.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

}


/**
 * Requests to edit this interaction response.
 *
 * @return The edited [Message] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicInteractionResponseBehavior.edit(builder: PublicInteractionResponseModifyBuilder.() -> Unit): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicInteractionResponseModifyBuilder().apply(builder)
    val message = kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder.toRequest())
    return Message(message.toData(), kord)
}

/**
 * Follows-up this interaction response with a [PublicFollowupMessage]
 *
 * @return created [PublicFollowupMessage]
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicInteractionResponseBehavior.followUp(builder: PublicFollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicFollowupMessageCreateBuilder().apply(builder)
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    return PublicFollowupMessage(Message(message.toData(), kord), applicationId, token, kord)
}

@KordPreview
fun PublicInteractionResponseBehavior(applicationId: Snowflake, token: String, kord: Kord) =
    object : PublicInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }
