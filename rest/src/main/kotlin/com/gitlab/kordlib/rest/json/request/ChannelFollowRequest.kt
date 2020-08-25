package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
class ChannelFollowRequest(
        @SerialName("webhook_channel_id")
        val webhookChannelId: String
)
