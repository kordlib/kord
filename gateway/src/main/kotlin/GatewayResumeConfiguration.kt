package dev.kord.gateway

import kotlinx.serialization.Serializable

@Serializable
public data class GatewayResumeConfiguration(
    val session: GatewaySession?,
    val startConfiguration: GatewayConfiguration
)