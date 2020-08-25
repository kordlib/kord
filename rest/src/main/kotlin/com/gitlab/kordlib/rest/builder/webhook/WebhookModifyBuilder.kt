package com.gitlab.kordlib.rest.builder.webhook

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.json.request.WebhookModifyRequest

@KordDsl
class WebhookModifyBuilder: AuditRequestBuilder<@OptIn(KordUnstableApi::class)  WebhookModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var avatar: Image? = null
    var channelId: Snowflake? = null

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): WebhookModifyRequest = WebhookModifyRequest(
            name = name,
            avatar = avatar?.dataUri,
            channelId = channelId?.value
    )
}