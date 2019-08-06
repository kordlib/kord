package com.gitlab.kordlib.core.`object`.builder.webhook

import com.gitlab.kordlib.core.`object`.Image
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ModifyWebhookRequest

class UpdateWebhookBuilder (
        var name: String? = null,
        var avatar: Image? = null,
        var channelId: Snowflake? = null
) {
    fun toRequest(): ModifyWebhookRequest = ModifyWebhookRequest(
            name = name!!,
            avatar = avatar!!.dataUri,
            channelId = channelId!!.value
    )
}