package dev.kord.rest.builder.webhook

import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.json.request.WebhookCreateRequest
import dev.kord.rest.Image

@KordDsl
class WebhookCreateBuilder(var name: String) : AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null

    private var _avatar: Optional<Image> = Optional.Missing()
    var avatar: Image? by ::_avatar.delegate()

    override fun toRequest() = WebhookCreateRequest(name, _avatar.map { it.dataUri })
}