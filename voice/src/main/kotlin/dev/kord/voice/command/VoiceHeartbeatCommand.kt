package dev.kord.voice.command

import kotlinx.serialization.Serializable

@Serializable
data class VoiceHeartbeatCommand(val nonce: Long) : VoiceCommand()