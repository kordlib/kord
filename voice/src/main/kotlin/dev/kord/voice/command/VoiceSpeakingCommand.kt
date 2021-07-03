package dev.kord.voice.command

import kotlinx.serialization.Serializable

@Serializable
data class VoiceSpeakingCommand(
    val speaking: Int,
    val delay: Int,
    val ssrc: Int
)
