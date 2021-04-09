package dev.kord.core.behavior.interaction

import behavior.interaction.EphemeralInteractionResponseBehavior
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.InteractionFollowup
import dev.kord.rest.builder.interaction.EphemeralFollowupMessageCreateBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface  EphemeralInteractionAcknowledgementBehavior : EphemeralInteractionResponseBehavior

@KordPreview
fun EphemeralInteractionAcknowledgementBehavior(applicationId: Snowflake, token: String, kord: Kord) =
    object : EphemeralInteractionAcknowledgementBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralInteractionAcknowledgementBehavior.followUp(
    content: String,
    builder: EphemeralFollowupMessageCreateBuilder.() -> Unit = {}
): InteractionFollowup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = EphemeralFollowupMessageCreateBuilder(content).apply(builder).toRequest()
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, request)
    val data = MessageData.from(response)
    return EphemeralFollowupMessage(Message(data, kord), applicationId, token, kord)
}
