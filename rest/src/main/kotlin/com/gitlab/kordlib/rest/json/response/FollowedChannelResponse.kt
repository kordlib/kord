package com.gitlab.kordlib.rest.json.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowedChannelResponse(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("webhook_id")
        val webhookId: String
)
