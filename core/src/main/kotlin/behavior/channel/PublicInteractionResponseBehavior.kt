package dev.kord.core.behavior.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionResponseBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
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
fun PublicInteractionResponseBehavior(applicationId: Snowflake, token: String, kord: Kord) =
    object : PublicInteractionResponseBehavior {
        override val applicationId: Snowflake
            get() = applicationId

        override val token: String
            get() = token

        override val kord: Kord
            get() = kord
    }


@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun PublicInteractionResponseBehavior.edit(builder: PublicInteractionResponseModifyBuilder.() -> Unit): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE)  }
    val request = PublicInteractionResponseModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyInteractionResponse(applicationId, token, request)
    return Message(response.toData(), kord)
}
