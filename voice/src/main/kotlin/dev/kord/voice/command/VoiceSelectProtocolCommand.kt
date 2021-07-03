package dev.kord.voice.command

import kotlinx.serialization.Serializable

@Serializable
data class VoiceSelectProtocolCommand(val protocol: String, val data: VoiceSelectProtocolCommandData) : VoiceCommand()

@Serializable
data class VoiceSelectProtocolCommandData(
    val address: String,
    val port: Int,
    val mode: String,
)