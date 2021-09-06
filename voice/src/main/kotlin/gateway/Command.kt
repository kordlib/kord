package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.voice.EncryptionMode
import dev.kord.voice.SpeakingFlags
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

sealed class Command {
    companion object : SerializationStrategy<Command> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Command") {
            element("op", OpCode.descriptor)
            element("d", JsonObject.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: Command) {
            val composite = encoder.beginStructure(descriptor)

            when (value) {
                is Identify -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, Identify.serializer(), value)
                }
                is Heartbeat -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Heartbeat)
                    composite.encodeLongElement(descriptor, 1, value.nonce)
                }
                is SendSpeaking -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Speaking)
                    composite.encodeSerializableElement(descriptor, 1, SendSpeaking.serializer(), value)
                }
                is SelectProtocol -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.SelectProtocol)
                    composite.encodeSerializableElement(descriptor, 1, SelectProtocol.serializer(), value)
                }
                is Resume -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, Resume.serializer(), value)
                }
            }

            composite.endStructure(descriptor)
        }
    }
}

@Serializable
data class Identify(
    @SerialName("server_id")
    val serverId: Snowflake,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("session_id")
    val sessionId: String,
    val token: String
) : Command()

@Serializable
data class Heartbeat(val nonce: Long) : Command()

@Serializable
data class SendSpeaking(
    val speaking: SpeakingFlags,
    val delay: Int,
    val ssrc: UInt
) : Command()

@Serializable
data class SelectProtocol(
    val protocol: String,
    val data: Data
) : Command() {
    @Serializable
    data class Data(
        val address: String,
        val port: Int,
        val mode: EncryptionMode
    )
}

@Serializable
data class Resume(
    val serverId: Snowflake,
    val sessionId: String,
    val token: String
) : Command()