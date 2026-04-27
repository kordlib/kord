package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.EncryptionMode
import dev.kord.voice.SpeakingFlags
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
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
                    composite.encodeSerializableElement(descriptor, 1, Heartbeat.serializer(), value)
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
                is DaveProtocolReadyForTransition -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.DaveProtocolReadyForTransition)
                    composite.encodeSerializableElement(descriptor, 1, DaveProtocolReadyForTransition.serializer(), value)
                }
                is DaveMlsInvalidCommitWelcome -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.DaveMlsInvalidCommitWelcome)
                    composite.encodeSerializableElement(descriptor, 1, DaveMlsInvalidCommitWelcome.serializer(), value)
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
    val token: String,
    @SerialName("max_dave_protocol_version")
    val maxDaveProtocolVersion: Int,
) : Command()

@Serializable
public data class Heartbeat(
    val t: Long,
    @SerialName("seq_ack") val seqAck: Long
) : Command()

@KordVoice
@Serializable
public data class SendSpeaking(
    val speaking: SpeakingFlags,
    val delay: Int,
    val ssrc: UInt
) : Command()

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
    @SerialName("server_id")
    val serverId: Snowflake,
    @SerialName("session_id")
    val sessionId: String,
    val token: String,
    @SerialName("max_dave_protocol_version")
    val maxDaveProtocolVersion: Int = 0
) : Command()

@Serializable
public data class DaveProtocolReadyForTransition(
    @SerialName("transition_id")
    val transitionId: Int
) : Command()

@Serializable
public data class DaveMlsInvalidCommitWelcome(
    @SerialName("transition_id")
    val transitionId: Int
) : Command()
