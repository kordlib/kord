package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice

@KordVoice
public data class VoiceGatewayConfiguration(
    val token: String,
    val endpoint: String
)