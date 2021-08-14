package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake

data class VoiceGatewayConfiguration(
    val channelId: Snowflake,
    val token: String,
    val sessionId: String,
    val endpoint: String
)