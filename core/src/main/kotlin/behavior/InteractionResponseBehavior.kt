package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.FollowupMessage
import dev.kord.rest.builder.interaction.FollowupMessageCreateBuilder
import dev.kord.rest.builder.interaction.OriginalInteractionResponseModifyBuilder

interface InteractionResponseBehavior : KordObject {
    val applicationId: Snowflake
    val token: String

    suspend fun edit(builder: OriginalInteractionResponseModifyBuilder.() -> Unit) {
        val request = OriginalInteractionResponseModifyBuilder().apply(builder).toRequest()
        kord.rest.interaction.modifyInteractionResponse(applicationId, token, request)
    }

    suspend fun delete() {
        kord.rest.interaction.deleteOriginalInteractionResponse(applicationId, token)
    }

    suspend fun followUp(builder: FollowupMessageCreateBuilder.() -> Unit): FollowupMessage {
        val request = FollowupMessageCreateBuilder().apply(builder).toRequest()
        val response = kord.rest.interaction.createFollowupMessage(applicationId, token, request)
        val data = MessageData.from(response)
        return FollowupMessage(data, token, applicationId, kord)
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