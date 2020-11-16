package com.gitlab.kordlib.rest.builder.webhook

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.common.entity.optional.map
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest
import com.gitlab.kordlib.rest.Image

@KordDsl
class WebhookCreateBuilder(var name: String): AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null

    private var _avatar: Optional<Image> = Optional.Missing()
    var avatar: Image? by ::_avatar.delegate() 

    override fun toRequest() = WebhookCreateRequest(name, _avatar.map { it.dataUri })
}