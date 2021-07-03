package dev.kord.voice.event

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder

@Serializable(with = VoiceHeartbeatACKEvent.Serializer)
data class VoiceHeartbeatACKEvent(
    val heartbeatInterval: Long
) : VoiceEvent() {
    companion object Serializer : DeserializationStrategy<VoiceHeartbeatACKEvent> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VoiceHeartbeatACK", PrimitiveKind.LONG)

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): VoiceHeartbeatACKEvent {
            return VoiceHeartbeatACKEvent(decoder.decodeLong())
        }
    }

}