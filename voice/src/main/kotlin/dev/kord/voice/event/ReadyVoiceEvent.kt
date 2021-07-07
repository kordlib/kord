package dev.kord.voice.event

import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReadyVoiceEvent(@SerialName("d") val ready: Ready) : VoiceEvent() {
    @Serializable
    data class Ready(
        val ssrc: Int,
        val ip: String,
        val port: Int,
        val modes: Set<String>
    )

    override val op: VoiceOpCode = VoiceOpCode.Ready
}