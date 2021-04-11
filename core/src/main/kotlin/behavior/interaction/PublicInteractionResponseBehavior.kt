package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.interaction.PublicInteractionResponseModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface PublicInteractionResponseBehavior : InteractionResponseBehavior {

    suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

}


@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicInteractionResponseBehavior.edit(builder: PublicInteractionResponseModifyBuilder.() -> Unit): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicInteractionResponseModifyBuilder().apply(builder)
    val message = kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder.toRequest())
    return Message(message.toData(), kord)
}

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicInteractionResponseBehavior.followup(wait: Boolean = false, builder: PublicFollowupMessageCreateBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicFollowupMessageCreateBuilder().apply(builder)
    val message = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest(), wait)
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
