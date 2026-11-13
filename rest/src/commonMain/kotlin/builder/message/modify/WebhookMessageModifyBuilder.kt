package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.MultipartWebhookEditMessageRequest
import dev.kord.rest.json.request.WebhookEditMessageRequest

@KordDsl
public class WebhookMessageModifyBuilder :
    AbstractMessageModifyBuilder(),
    RequestBuilder<MultipartWebhookEditMessageRequest> {
    // see https://discord.com/developers/docs/resources/webhook#edit-webhook-message
    override fun toRequest(): MultipartWebhookEditMessageRequest = MultipartWebhookEditMessageRequest(
        request = WebhookEditMessageRequest(
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
