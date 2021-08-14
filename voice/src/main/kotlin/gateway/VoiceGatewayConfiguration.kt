package dev.kord.voice.gateway

data class VoiceGatewayConfiguration(
    val token: String,
    val sessionId: String,
    val endpoint: String
)