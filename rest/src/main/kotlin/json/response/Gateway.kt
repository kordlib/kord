package dev.kord.rest.json.response

import dev.kord.common.serialization.DurationInWholeMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public data class GatewayResponse(val url: String)

@Serializable
public data class BotGatewayResponse(
    val url: String,
    val shards: Int,
    @SerialName("session_start_limit")
    val sessionStartLimit: SessionStartLimitResponse
)

@Serializable
public data class SessionStartLimitResponse(
    val total: Int,
    val remaining: Int,
    @SerialName("reset_after")
    @Serializable(with = DurationInWholeMillisecondsSerializer::class)
    val resetAfter: Duration,
    @SerialName("max_concurrency")
    val maxConcurrency: Int
)
