package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.rest.builder.interaction.EphemeralInteractionResponseModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface EphemeralInteractionResponseBehavior : InteractionResponseBehavior


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


@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralInteractionResponseBehavior.edit(builder: EphemeralInteractionResponseModifyBuilder.() -> Unit): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = EphemeralInteractionResponseModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyInteractionResponse(applicationId, token, request)
    return Message(response.toData(), kord)
}