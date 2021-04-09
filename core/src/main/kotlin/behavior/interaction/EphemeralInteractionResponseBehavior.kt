package behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.InteractionFollowup
import dev.kord.rest.builder.interaction.EphemeralFollowupMessageCreateBuilder
import dev.kord.rest.builder.interaction.EphemeralInteractionResponseModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralInteractionResponseBehavior.edit(builder: EphemeralInteractionResponseModifyBuilder.() -> Unit): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = EphemeralInteractionResponseModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyInteractionResponse(applicationId, token, request)
    return Message(response.toData(), kord)
}


