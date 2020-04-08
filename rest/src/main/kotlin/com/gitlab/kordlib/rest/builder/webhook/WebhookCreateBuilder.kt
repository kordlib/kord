package com.gitlab.kordlib.rest.builder.webhook

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest
import com.gitlab.kordlib.rest.Image

@KordDsl
class WebhookCreateBuilder: AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null
    lateinit var name: String
    var avatar: Image? = null

    override fun toRequest() = WebhookCreateRequest(name, avatar?.dataUri)
}