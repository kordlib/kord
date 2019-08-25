package com.gitlab.kordlib.core.`object`.builder.webhook

import com.gitlab.kordlib.core.`object`.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.`object`.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest

class WebhookCreateBuilder: AuditRequestBuilder<WebhookCreateRequest> {
    override var reason: String? = null
    lateinit var name: String
    var avatar: String? = null

    override fun toRequest() = WebhookCreateRequest(name, avatar)
}