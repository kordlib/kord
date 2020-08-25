package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class FollowedChannelResponse(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("webhook_id")
        val webhookId: String
)
