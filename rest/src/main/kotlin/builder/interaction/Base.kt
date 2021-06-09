package dev.kord.rest.builder.interaction

import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseModifyRequest

interface BaseInteractionResponseBuilder<T> : RequestBuilder<T> {
    var content: String?

    var embeds: MutableList<EmbedBuilder>?

    var allowedMentions: AllowedMentionsBuilder?

}

typealias BaseInteractionResponseCreateBuilder = BaseInteractionResponseBuilder<MultipartInteractionResponseCreateRequest>
typealias BaseInteractionResponseModifyBuilder = BaseInteractionResponseBuilder<MultipartInteractionResponseModifyRequest>
