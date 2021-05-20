package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StageInstanceCreateRequest(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val topic: String
)

@Serializable
data class StageInstanceUpdateRequest(val topic: String)
