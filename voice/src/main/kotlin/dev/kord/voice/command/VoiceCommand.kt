package dev.kord.voice.command

import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject


sealed class VoiceCommand {
    companion object : SerializationStrategy<VoiceCommand> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Command") {
            element("op", VoiceOpCode.descriptor)
            element("d", JsonObject.serializer().descriptor)
        }


        override fun serialize(encoder: Encoder, value: VoiceCommand) {
            val composite = encoder.beginStructure(descriptor)
            when (value) {
                is VoiceHeartbeatCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode.VoiceOpCodeSerializer, VoiceOpCode.Heartbeat)
                    composite.encodeSerializableElement(descriptor, 1, VoiceHeartbeatCommand.serializer(), value)
                }

                is VoiceResumeCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode.VoiceOpCodeSerializer, VoiceOpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, VoiceResumeCommand.serializer(), value)
                }

                is VoiceSelectProtocolCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode.VoiceOpCodeSerializer, VoiceOpCode.SelectProtocol)
                    composite.encodeSerializableElement(descriptor, 1, VoiceSelectProtocolCommand.serializer(), value)
                }

            }

            composite.endStructure(descriptor)
        }

    }

}