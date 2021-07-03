package dev.kord.voice.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VoiceResumeCommand(
    @SerialName("server_id")
    val serverId: String,
    @SerialName("session_id")
    val sessionId: String,
    val token: String
) : VoiceCommand()