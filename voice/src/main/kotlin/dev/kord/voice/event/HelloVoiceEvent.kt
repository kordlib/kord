package dev.kord.voice.event

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder

@Serializable
data class HelloVoiceEvent(
    val heartbeatInterval: Long
) : VoiceEvent() {
        companion object Serializer: SerializationStrategy<HelloVoiceEvent> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VoiceHeartbeat", PrimitiveKind.LONG)

            @OptIn(ExperimentalSerializationApi::class)
            override fun serialize(encoder: Encoder, value: HelloVoiceEvent) {
                encoder.encodeSerializableValue(Long.serializer(), value.heartbeatInterval)
            }
        }

    }