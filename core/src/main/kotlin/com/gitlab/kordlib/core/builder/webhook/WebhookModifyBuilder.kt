package com.gitlab.kordlib.core.builder.webhook

import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.entity.Image
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.WebhookModifyRequest

class WebhookModifyBuilder: AuditRequestBuilder<WebhookModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var avatar: Image? = null
    var channelId: Snowflake? = null

    override fun toRequest(): WebhookModifyRequest = WebhookModifyRequest(
            name = name,
            avatar = avatar?.dataUri,
            channelId = channelId?.value
    )
}