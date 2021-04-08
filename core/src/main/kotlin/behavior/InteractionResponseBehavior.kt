package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.InteractionFollowup
import dev.kord.rest.builder.interaction.FollowupMessageCreateBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface InteractionResponseBehavior : KordObject {
    val applicationId: Snowflake
    val token: String

}


@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionResponseBehavior.followUp(builder: FollowupMessageCreateBuilder.() -> Unit): InteractionFollowup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = FollowupMessageCreateBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, request)
    val data = MessageData.from(response)
    return InteractionFollowup(Message(data, kord), token, applicationId, kord)
}