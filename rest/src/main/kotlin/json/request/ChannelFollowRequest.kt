package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChannelFollowRequest(
    @SerialName("webhook_channel_id")
    val webhookChannelId: Snowflake,
)
