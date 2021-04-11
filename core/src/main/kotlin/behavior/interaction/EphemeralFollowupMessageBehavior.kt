package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.EphemeralFollowupMessageModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface EphemeralFollowupMessageBehavior : FollowupMessageBehavior

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralFollowupMessageBehavior.edit(builder: EphemeralFollowupMessageModifyBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralFollowupMessageModifyBuilder().apply(builder)
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder.toRequest())
    return PublicFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}