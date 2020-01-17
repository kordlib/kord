package com.gitlab.kordlib.rest.builder.webhook

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest

@KordDsl
class WebhookCreateBuilder: AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null
    lateinit var name: String
    var avatar: String? = null

    override fun toRequest() = WebhookCreateRequest(name, avatar)
}