package dev.kord.gateway

import kotlinx.serialization.Serializable

@Serializable
public data class GatewayResumeConfiguration(
    val sessionId: String,
    val resumeUrl: String,
    val sequence: Int,
    val startConfiguration: GatewayConfiguration
)