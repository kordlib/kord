package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.MessageEditPatchRequest
import dev.kord.rest.json.request.MultipartMessagePatchRequest

@KordDsl
public class UserMessageModifyBuilder : AbstractMessageModifyBuilder(), RequestBuilder<MultipartMessagePatchRequest> {
    // see https://discord.com/developers/docs/resources/channel#edit-message
    override fun toRequest(): MultipartMessagePatchRequest = MultipartMessagePatchRequest(
        requests = MessageEditPatchRequest(
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
