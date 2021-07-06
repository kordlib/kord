package dev.kord.voice.command

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder

@Serializable
data class VoiceHeartbeatCommand(val nonce: Long) : VoiceCommand() {
    companion object Serializer : SerializationStrategy<VoiceHeartbeatCommand> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VoiceHeartBeatCommand", PrimitiveKind.LONG)
        override fun serialize(encoder: Encoder, value: VoiceHeartbeatCommand) {
           encoder.encodeLong(value.nonce)
        }

    }

}