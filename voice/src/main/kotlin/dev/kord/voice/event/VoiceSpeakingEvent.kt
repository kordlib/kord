package dev.kord.voice.event

import dev.kord.common.entity.Snowflake
import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VoiceSpeakingEvent(@SerialName("d") val speaking: Speaking) : VoiceEvent() {
    @Serializable
    data class Speaking(
        // bitset of speaking
        val speaking: Int,
        val delay: Int,
        val ssrc: Int
    )

    override val op: VoiceOpCode = VoiceOpCode.Speaking
}