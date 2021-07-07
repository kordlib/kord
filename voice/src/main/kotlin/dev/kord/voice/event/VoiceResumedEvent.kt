package dev.kord.voice.event

import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.Serializable

@Serializable
object VoiceResumedEvent : VoiceEvent() {
    override val op: VoiceOpCode = VoiceOpCode.Resumed
}