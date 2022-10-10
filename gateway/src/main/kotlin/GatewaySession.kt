package dev.kord.gateway

import kotlinx.serialization.Serializable

@Serializable
public data class GatewaySession(
    val sessionId: String,
    val resumeUrl: String,
    val sequence: Int
)