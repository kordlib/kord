package dev.kord.voice.event

import kotlinx.serialization.Serializable

@Serializable
data class ReadyVoiceEvent(
    val ssrc: Int,
    val ip: String,
    val port: Int,
    val modes: List<String>,
) : VoiceEvent()