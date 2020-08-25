package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class GatewayResponse(val url: String, val shards: Int)

@Serializable
@KordUnstableApi
data class BotGatewayResponse(
        val url: String,
        val shards: Int,
        @SerialName("session_start_limit")
        val sessionStartLimit: SessionStartLimitResponse
)

@Serializable
@KordUnstableApi
data class SessionStartLimitResponse(
        val total: Int,
        val remaining: Int,
        @SerialName("reset_after")
        val resetAfter: Int
)