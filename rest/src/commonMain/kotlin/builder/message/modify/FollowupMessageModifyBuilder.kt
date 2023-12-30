package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.FollowupMessageModifyRequest
import dev.kord.rest.json.request.MultipartFollowupMessageModifyRequest

@KordDsl
public class FollowupMessageModifyBuilder :
    AbstractMessageModifyBuilder(),
    RequestBuilder<MultipartFollowupMessageModifyRequest> {
    // see https://discord.com/developers/docs/interactions/receiving-and-responding#edit-followup-message
    override fun toRequest(): MultipartFollowupMessageModifyRequest = MultipartFollowupMessageModifyRequest(
        request = FollowupMessageModifyRequest(
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
