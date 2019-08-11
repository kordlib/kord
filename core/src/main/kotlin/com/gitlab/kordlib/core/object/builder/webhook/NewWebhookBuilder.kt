package com.gitlab.kordlib.core.`object`.builder.webhook

import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest

class NewWebhookBuilder (
        var name: String? = null,
        var avatar: String? = null
) {
    fun toRequest() = WebhookCreateRequest(name!!, avatar)
}