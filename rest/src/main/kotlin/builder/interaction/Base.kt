package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseModifyRequest

sealed interface BaseInteractionResponseBuilder<T> : RequestBuilder<T> {
    var content: String?

    var embeds: MutableList<EmbedBuilder>?

    var allowedMentions: AllowedMentionsBuilder?

}

@KordPreview
interface BaseInteractionResponseCreateBuilder : BaseInteractionResponseBuilder<MultipartInteractionResponseCreateRequest>
@KordPreview
interface BaseInteractionResponseModifyBuilder : BaseInteractionResponseBuilder<MultipartInteractionResponseModifyRequest>
