package dev.kord.voice.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceIdentifyCommand(
    @SerialName("server_id")
    val serverId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("session_id")
    val sessionId: String,
    val token: String
) : VoiceCommand()