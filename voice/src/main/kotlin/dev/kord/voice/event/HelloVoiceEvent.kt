package dev.kord.voice.event

import dev.kord.voice.VoiceOpCode
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
class HelloVoiceEvent(@SerialName("d") val hello: Hello) : VoiceEvent() {
    @Serializable
    data class Hello(
        @SerialName("v") val version: Int,
        @SerialName("heartbeat_interval") val heartHeatInterval: Double
    )

    override val op: VoiceOpCode = VoiceOpCode.Hello
}