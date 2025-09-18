package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.EncryptionMode
import dev.kord.voice.SpeakingFlags
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.SerializationStrategy as KSerializationStrategy

public sealed class Command {
    public object SerializationStrategy : KSerializationStrategy<Command> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Command") {
            element("op", OpCode.serializer().descriptor)
            element("d", JsonObject.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: Command) {
            val composite = encoder.beginStructure(descriptor)

            when (value) {
                is Identify -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, Identify.serializer(), value)
                }
                is Heartbeat -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Heartbeat)
                    composite.encodeLongElement(descriptor, 1, value.nonce)
                }
                is MediaSinkWants -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.Serializer, OpCode.MediaSinkWants)
                    composite.encodeSerializableElement(descriptor, 1, MediaSinkWants.Serializer, value)
                }
                is SendSpeaking -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Speaking)
                    composite.encodeSerializableElement(descriptor, 1, SendSpeaking.serializer(), value)
                }
                is SelectProtocol -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.SelectProtocol)
                    composite.encodeSerializableElement(descriptor, 1, SelectProtocol.serializer(), value)
                }
                is Resume -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, Resume.serializer(), value)
                }
            }

            composite.endStructure(descriptor)
        }
    }
}

@Serializable
public data class Identify(
    @SerialName("server_id")
    val serverId: Snowflake,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("session_id")
    val sessionId: String,
    val token: String
) : Command()

@Serializable
public data class Heartbeat(val nonce: Long) : Command()

@KordVoice
@Serializable
public data class SendSpeaking(
    val speaking: SpeakingFlags,
    val delay: Int,
    val ssrc: UInt
) : Command()

@KordVoice
@Serializable
public data class MediaSinkWants(
    /**
     * Control whether the client wants to receive audio packets from **ANY** SSRC.
     */
    val wants: Boolean,
    /**
     * Control whether the client wants to receive audio packets from a specific SSRC.
     */
    val ssrcs: Map<UInt, Boolean> = emptyMap(),
) : Command() {
    public companion object Serializer : KSerializationStrategy<MediaSinkWants> {
        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor =
            SerialDescriptor("MediaSinkWants", JsonObject.serializer().descriptor)

        override fun serialize(encoder: Encoder, value: MediaSinkWants) {
            val obj = buildJsonObject {
                put("any", if (value.wants) 100 else 0)
                value.ssrcs.forEach { (ssrc, wants) -> put(ssrc.toString(), if (wants) 100 else 0) }
            }

            encoder.encodeSerializableValue(JsonObject.serializer(), obj)
        }
    }
}

@KordVoice
@Serializable
public data class SelectProtocol(
    val protocol: String,
    val data: Data
) : Command() {
    @KordVoice
    @Serializable
    public data class Data(
        val address: String,
        val port: Int,
        val mode: EncryptionMode
    )
}

@Serializable
public data class Resume(
    val serverId: Snowflake,
    val sessionId: String,
    val token: String
) : Command()
