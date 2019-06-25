package com.gitlab.hopebaron.rest.json.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayResponse(val url: String, val shards: Int)

@Serializable
data class SessionStartLimitResponse(
        val total: Int,
        val remaining: Int,
        @SerialName("reset_after")
        val resetAfter: Int
)