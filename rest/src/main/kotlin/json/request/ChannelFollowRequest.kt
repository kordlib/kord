package dev.kord.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ChannelFollowRequest(
        @SerialName("webhook_channel_id")
        val webhookChannelId: String
)
