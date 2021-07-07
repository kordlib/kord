package dev.kord.voice.event

import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder

@Serializable
data class VoiceHeartbeatACKEvent(
    @SerialName("d") val heartbeatInterval: Double
) : VoiceEvent() {
    override val op: VoiceOpCode = VoiceOpCode.HeartbeatACK
}