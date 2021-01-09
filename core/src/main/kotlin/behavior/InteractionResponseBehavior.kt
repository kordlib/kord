package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.FollowupMessage
import dev.kord.rest.builder.interaction.FollowupMessageCreateBuilder
import dev.kord.rest.builder.interaction.OriginalInteractionResponseModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface BaseInteractionResponseBehavior: KordObject {
    val applicationId: Snowflake
    val token: String

}
interface InteractionResponseBehavior : BaseInteractionResponseBehavior {
    suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

    companion object {
        operator fun invoke(applicationId: Snowflake, token: String, kord: Kord) =
            object : InteractionResponseBehavior {
                override val applicationId: Snowflake
                    get() = applicationId

                override val token: String
                    get() = token

                override val kord: Kord
                    get() = kord
            }
    }
}

interface InteractionAcknowledgementBehavior : BaseInteractionResponseBehavior {

    companion object {
        operator fun invoke(applicationId: Snowflake, token: String, kord: Kord) =
            object : InteractionAcknowledgementBehavior {
                override val applicationId: Snowflake
                    get() = applicationId

                override val token: String
                    get() = token

                override val kord: Kord
                    get() = kord
            }
    }

}

@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionResponseBehavior.edit(builder: OriginalInteractionResponseModifyBuilder.() -> Unit): Message {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = OriginalInteractionResponseModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyInteractionResponse(applicationId, token, request)
    return Message(response.toData(), kord)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun BaseInteractionResponseBehavior.followUp(builder: FollowupMessageCreateBuilder.() -> Unit): FollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = FollowupMessageCreateBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.createFollowupMessage(applicationId, token, request)
    val data = MessageData.from(response)
    return FollowupMessage(data, token, applicationId, kord)
}