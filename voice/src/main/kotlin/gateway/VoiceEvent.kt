package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.voice.EncryptionMode
import dev.kord.voice.SpeakingFlags
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.DeserializationStrategy as KDeserializationStrategy

private val jsonLogger = KotlinLogging.logger { }

public sealed class VoiceEvent {
    public object DeserializationStrategy : KDeserializationStrategy<VoiceEvent?> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.kord.voice.gateway.Event") {
            element("op", OpCode.serializer().descriptor)
            element("d", JsonElement.serializer().descriptor, isOptional = true)
        }

        override fun deserialize(decoder: Decoder): VoiceEvent? = decoder.decodeStructure(descriptor) {
            var op: OpCode? = null
            var d: JsonElement? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> op = decodeSerializableElement(descriptor, index, OpCode.serializer(), op)
                    1 -> d = decodeSerializableElement(descriptor, index, JsonElement.serializer(), d)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }
            when (op) {
                null ->
                    throw @OptIn(ExperimentalSerializationApi::class) MissingFieldException("op", descriptor.serialName)
                OpCode.Ready -> decodeEvent(decoder, op, Ready.serializer(), d)
                OpCode.SessionDescription -> decodeEvent(decoder, op, SessionDescription.serializer(), d)
                OpCode.Speaking -> decodeEvent(decoder, op, Speaking.serializer(), d)
                OpCode.HeartbeatAck -> decodeEvent(decoder, op, HeartbeatAck.serializer(), d)
                OpCode.Hello -> decodeEvent(decoder, op, Hello.serializer(), d)
                OpCode.Resumed -> {
                    // ignore the d field, Resumed is supposed to have null here:
                    // https://discord.com/developers/docs/topics/voice-connections#resuming-voice-connection-example-resumed-payload
                    null
                }
                OpCode.ClientsConnect -> decodeEvent(decoder, op, ClientsConnect.serializer(), d)
                OpCode.ClientDisconnect -> decodeEvent(decoder, op, ClientDisconnect.serializer(), d)
                OpCode.DaveProtocolPrepareTransition -> decodeEvent(decoder, op, DaveProtocolPrepareTransition.serializer(), d)
                OpCode.DaveProtocolExecuteTransition -> decodeEvent(decoder, op, DaveProtocolExecuteTransition.serializer(), d)
                OpCode.DaveProtocolPrepareEpoch -> decodeEvent(decoder, op, DaveProtocolPrepareEpoch.serializer(), d)
                OpCode.Identify, OpCode.SelectProtocol, OpCode.Heartbeat, OpCode.Resume,
                OpCode.DaveMlsExternalSenderPackage, OpCode.DaveMlsKeyPackage, OpCode.DaveMlsProposals,
                OpCode.DaveMlsCommitWelcome, OpCode.DaveMlsAnnounceCommitTransition, OpCode.DaveMlsWelcome,
                OpCode.DaveMlsInvalidCommitWelcome, OpCode.DaveProtocolReadyForTransition,
                OpCode.Unknown,
                -> {
                    jsonLogger.debug { "Unknown voice gateway event with opcode $op : $d" }
                    null
                }
            }
        }

        private fun <T> decodeEvent(
            decoder: Decoder,
            op: OpCode,
            deserializer: KDeserializationStrategy<T>,
            d: JsonElement?,
        ): T {
            requireNotNull(d) { "Voice gateway event is missing 'd' field for opcode $op" }
            // this cast will always succeed, otherwise decoder couldn't have decoded d
            return (decoder as JsonDecoder).json.decodeFromJsonElement(deserializer, d)
        }
    }
}

@Serializable
public data class Ready(
    val ssrc: UInt,
    val ip: String,
    val port: Int,
    val modes: List<EncryptionMode>,
    @SerialName("auth_session_id")
    val authSessionId: String = ""
) : VoiceEvent()

@Serializable
public data class Hello(
    @SerialName("v")
    val version: Short,
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Double
) : VoiceEvent()

@Serializable
public data class HeartbeatAck(@SerialName("t") val nonce: Long) : VoiceEvent()

@Serializable
public data class SessionDescription(
    val mode: EncryptionMode,
    @SerialName("secret_key")
    val secretKey: List<UByte>,
    @SerialName("dave_protocol_version")
    val daveProtocolVersion: Int = 0
) : VoiceEvent()

@Serializable
public data class Speaking(
    @SerialName("user_id")
    val userId: Snowflake,
    val ssrc: UInt,
    val speaking: SpeakingFlags
) : VoiceEvent()

public sealed class Close : VoiceEvent() {
    /**
     * The user closed the Gateway connection.
     */
    public object UserClose : Close()

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    public object Timeout : Close()

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    public data class DiscordClose(val closeCode: VoiceGatewayCloseCode, val recoverable: Boolean) : Close()

    /**
     * The gateway closed and will attempt to resume the session.
     */
    public object Reconnecting : Close()

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [VoiceGateway.start], otherwise all resources linked to the Gateway should free.
     */
    public object RetryLimitReached : Close()
}

@Serializable
public data class ClientsConnect(
    @SerialName("user_ids")
    val userIds: List<Snowflake>
) : VoiceEvent()

@Serializable
public data class ClientDisconnect(
    @SerialName("user_id")
    val userId: Snowflake
) : VoiceEvent()

@Serializable
public data class DaveProtocolPrepareTransition(
    @SerialName("protocol_version")
    val protocolVersion: Int,
    @SerialName("transition_id")
    val transitionId: Int
) : VoiceEvent()

@Serializable
public data class DaveProtocolExecuteTransition(
    @SerialName("transition_id")
    val transitionId: Int
) : VoiceEvent()

@Serializable
public data class DaveProtocolPrepareEpoch(
    @SerialName("protocol_version")
    val protocolVersion: Int,
    val epoch: Int
) : VoiceEvent()

public data class DaveMlsExternalSenderPackage(
    val data: ByteArray
) : VoiceEvent() {
    override fun equals(other: Any?): Boolean = other is DaveMlsExternalSenderPackage && data.contentEquals(other.data)
    override fun hashCode(): Int = data.contentHashCode()
}

public data class DaveMlsProposals(
    val data: ByteArray
) : VoiceEvent() {
    override fun equals(other: Any?): Boolean = other is DaveMlsProposals && data.contentEquals(other.data)
    override fun hashCode(): Int = data.contentHashCode()
}

public data class DaveMlsAnnounceCommitTransition(
    val transitionId: Int,
    val data: ByteArray
) : VoiceEvent() {
    override fun equals(other: Any?): Boolean = other is DaveMlsAnnounceCommitTransition && transitionId == other.transitionId && data.contentEquals(other.data)
    override fun hashCode(): Int = 31 * transitionId + data.contentHashCode()
}

public data class DaveMlsWelcome(
    val transitionId: Int,
    val data: ByteArray
) : VoiceEvent() {
    override fun equals(other: Any?): Boolean = other is DaveMlsWelcome && transitionId == other.transitionId && data.contentEquals(other.data)
    override fun hashCode(): Int = 31 * transitionId + data.contentHashCode()
}
