package com.gitlab.kordlib.core.builder.webhook

import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.builder.KordBuilder
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest

@KordBuilder
class WebhookCreateBuilder: AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null
    lateinit var name: String
    var avatar: String? = null

    override fun toRequest() = WebhookCreateRequest(name, avatar)
}