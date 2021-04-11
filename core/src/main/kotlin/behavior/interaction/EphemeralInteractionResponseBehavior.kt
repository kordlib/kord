package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.EphemeralFollowupMessageCreateBuilder
import dev.kord.rest.builder.interaction.EphemeralInteractionResponseModifyBuilder
import dev.kord.rest.builder.interaction.PublicFollowupMessageCreateBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralInteractionResponseBehavior.edit(builder: EphemeralInteractionResponseModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralInteractionResponseModifyBuilder().apply(builder)
    kord.rest.interaction.modifyInteractionResponse(applicationId, token, builder.toRequest())
}

@OptIn(ExperimentalContracts::class)
@KordPreview
suspend inline fun EphemeralInteractionResponseBehavior.followup(
    content: String,
    builder: EphemeralFollowupMessageCreateBuilder.() -> Unit = {}
): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralFollowupMessageCreateBuilder(content).apply(builder)
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    val message = Message(response.toData(), kord)
    return EphemeralFollowupMessage(message, applicationId, token, kord)
}


@OptIn(ExperimentalContracts::class)
@KordPreview
@KordUnsafe
suspend inline fun EphemeralInteractionResponseBehavior.followup(
    builder: PublicFollowupMessageCreateBuilder.() -> Unit
): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = PublicFollowupMessageCreateBuilder().apply(builder)
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, builder.toRequest())
    val message = Message(response.toData(), kord)
    return PublicFollowupMessage(message, applicationId, token, kord)
}

@KordPreview
fun EphemeralInteractionResponseBehavior(applicationId: Snowflake, token: String, kord: Kord) =
    object : EphemeralInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }
