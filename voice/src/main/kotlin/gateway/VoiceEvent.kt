package dev.kord.voice.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.voice.EncryptionMode
import dev.kord.voice.SpeakingFlags
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
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
                    Resumed
                }
                OpCode.Identify, OpCode.SelectProtocol, OpCode.Heartbeat, OpCode.Resume, OpCode.ClientDisconnect,
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
    val modes: List<EncryptionMode>
) : VoiceEvent()

@Serializable
public data class Hello(
    @SerialName("v")
    val version: Short,
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Double
) : VoiceEvent()

@Serializable(with = HeartbeatAck.Serializer::class)
public data class HeartbeatAck(val nonce: Long) : VoiceEvent() {
    internal object Serializer : KSerializer<HeartbeatAck> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.voice.gateway.HeartbeatAck", PrimitiveKind.LONG)
        override fun serialize(encoder: Encoder, value: HeartbeatAck) = encoder.encodeLong(value.nonce)
        override fun deserialize(decoder: Decoder) = HeartbeatAck(nonce = decoder.decodeLong())
    }
}

@Serializable
public data class SessionDescription(
    val mode: EncryptionMode,
    @SerialName("secret_key")
    val secretKey: List<UByte>
) : VoiceEvent()

@Serializable
public data class Speaking(
    @SerialName("user_id")
    val userId: Snowflake,
    val ssrc: UInt,
    val speaking: SpeakingFlags
) : VoiceEvent()

public object Resumed : VoiceEvent() {
    @Deprecated(
        "'Resumed' is no longer serializable, deserialize it with 'VoiceEvent.DeserializationStrategy' instead. " +
            "Deprecated without a replacement.",
        level = DeprecationLevel.ERROR,
    )
    public fun serializer(): KSerializer<Resumed> = serializer

    private val serializer: KSerializer<Resumed> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        @Suppress("INVISIBLE_MEMBER")
        kotlinx.serialization.internal.ObjectSerializer(
            serialName = "dev.kord.voice.gateway.Resumed",
            objectInstance = Resumed,
            classAnnotations = arrayOf(),
        )
    }
}

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
