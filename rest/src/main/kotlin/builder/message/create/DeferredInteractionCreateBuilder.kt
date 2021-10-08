package dev.kord.rest.builder.message.create

import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest

class DeferredInteractionCreateBuilder(var ephemeral: Boolean = false): RequestBuilder<InteractionResponseCreateRequest> {
    override fun toRequest(): InteractionResponseCreateRequest {
        return InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredChannelMessageWithSource,
            data = Optional(
                InteractionApplicationCommandCallbackData(
                    flags = Optional(if(ephemeral) MessageFlags(MessageFlag.Ephemeral) else null).coerceToMissing()
                )
            )
        )
    }

}