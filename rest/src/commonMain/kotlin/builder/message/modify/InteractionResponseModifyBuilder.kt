package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.InteractionResponseModifyRequest
import dev.kord.rest.json.request.MultipartInteractionResponseModifyRequest

@KordDsl
public class InteractionResponseModifyBuilder :
    AbstractMessageModifyBuilder(),
    RequestBuilder<MultipartInteractionResponseModifyRequest> {
    // see https://discord.com/developers/docs/interactions/receiving-and-responding#edit-original-interaction-response
    override fun toRequest(): MultipartInteractionResponseModifyRequest = MultipartInteractionResponseModifyRequest(
        request = InteractionResponseModifyRequest(
            content = _content,
            embeds = _embeds.mapList { it.toRequest() },
            flags = buildFlags(),
            allowedMentions = _allowedMentions.map { it.build() },
            components = _components.mapList { it.build() },
            attachments = _attachments.mapList { it.toRequest() },
        ),
        files = files.toList(),
    )
}
