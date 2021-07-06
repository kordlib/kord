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
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Heartbeat)
                    composite.encodeSerializableElement(descriptor, 1, VoiceHeartbeatCommand, value)
                }

                is VoiceResumeCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, VoiceResumeCommand.serializer(), value)
                }

                is VoiceSelectProtocolCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.SelectProtocol)
                    composite.encodeSerializableElement(descriptor, 1, VoiceSelectProtocolCommand.serializer(), value)
                }
                is VoiceIdentifyCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, VoiceIdentifyCommand.serializer(), value)
                }
                is VoiceSpeakingCommand -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Speaking)
                    composite.encodeSerializableElement(descriptor, 1, VoiceSpeakingCommand.serializer(), value)
                }
            }

            composite.endStructure(descriptor)
        }

    }

}