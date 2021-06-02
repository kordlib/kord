package dev.kord.rest.json.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayResponse(val url: String, val shards: Int)

@Serializable
data class BotGatewayResponse(
    val url: String,
    val shards: Int,
    @SerialName("session_start_limit")
    val sessionStartLimit: SessionStartLimitResponse
)

@Serializable
data class SessionStartLimitResponse(
    val total: Int,
    val remaining: Int,
    @SerialName("reset_after")
    val resetAfter: Int,
    @SerialName("max_concurrency")
    val maxConcurrency: Int
)
