package dev.kord.voice.event

import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SessionDescriptionEvent(@SerialName("d") val sessionDescription: SessionDescription) : VoiceEvent() {
    @Serializable
    class SessionDescription @OptIn(ExperimentalUnsignedTypes::class) constructor(
        val mode: String,
        @SerialName("secret_key") val secretKey: Array<UByte>
    )

    override val op: VoiceOpCode = VoiceOpCode.SessionDescription
}