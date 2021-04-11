package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface PublicFollowupMessageBehavior : FollowupMessageBehavior {

    suspend fun delete() {
        kord.rest.interaction.deleteFollowupMessage(applicationId, token, id)
    }
}

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicFollowupMessageBehavior.edit(builder: PublicFollowupMessageModifyBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicFollowupMessageModifyBuilder().apply(builder)
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder.toRequest())
    return PublicFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}