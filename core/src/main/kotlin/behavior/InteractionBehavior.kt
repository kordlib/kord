package dev.kord.core.behavior

import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optional
import dev.kord.core.Kord
import dev.kord.core.entity.KordEntity
import dev.kord.rest.builder.interaction.InteractionApplicationCommandCallbackDataBuilder
import dev.kord.rest.json.request.InteractionResponseCreateRequest

interface InteractionBehavior : KordEntity {

    //TODO("return full response with full functionality")

    val applicationId: Snowflake
    val token: String

    suspend fun acknowledge(source: Boolean = false): InteractionResponseBehavior {
        val type = if (source) InteractionResponseType.ACKWithSource
        else InteractionResponseType.Acknowledge
        val request = InteractionResponseCreateRequest(type)
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return InteractionResponseBehavior(applicationId, token,kord)
    }



    suspend fun respond(
        content: String,
        source: Boolean = false,
        builder: InteractionApplicationCommandCallbackDataBuilder.() -> Unit
    ): InteractionResponseBehavior {
        val type = if (source) InteractionResponseType.ChannelMessageWithSource
        else InteractionResponseType.ChannelMessage

        val data = InteractionApplicationCommandCallbackDataBuilder(content).apply(builder).build()
        val request = InteractionResponseCreateRequest(type, data.optional())
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return InteractionResponseBehavior(applicationId, token,kord)

    }


    operator fun invoke(id: Snowflake, token: String, applicationId: Snowflake, kord: Kord) =
        object : InteractionBehavior {
            override val id: Snowflake
                get() = id

            override val token: String
                get() = token

            override val applicationId: Snowflake
                get() = applicationId

            override val kord: Kord
                get() = kord
        }
}

interface PartialInteractionBehavior : KordEntity {
    //TODO("return full response with data")
    val token: String

    suspend fun acknowledge(source: Boolean = false) {
        val type = if (source) InteractionResponseType.ACKWithSource else InteractionResponseType.Acknowledge
        val request = InteractionResponseCreateRequest(type)
        kord.rest.interaction.createInteractionResponse(id, token, request)
    }



    suspend fun respond(
        content: String,
        source: Boolean = false,
        builder: InteractionApplicationCommandCallbackDataBuilder.() -> Unit
    ) {
        val type =
            if (source) InteractionResponseType.ChannelMessageWithSource else InteractionResponseType.ChannelMessage
        val data = InteractionApplicationCommandCallbackDataBuilder(content).apply(builder).build()
        val request = InteractionResponseCreateRequest(type, data.optional())
        kord.rest.interaction.createInteractionResponse(id, token, request)
    }


    companion object {
        operator fun invoke(id: Snowflake, token: String, kord: Kord) = object : PartialInteractionBehavior {
            override val id: Snowflake
                get() = id
            override val token: String
                get() = token
            override val kord: Kord
                get() = kord
        }
    }
}