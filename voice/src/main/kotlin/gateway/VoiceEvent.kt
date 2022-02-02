@file:OptIn(KordVoice::class)

package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.voice.EncryptionMode
import dev.kord.voice.SpeakingFlags
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging
import kotlinx.serialization.DeserializationStrategy as KDeserializationStrategy

private val jsonLogger = KotlinLogging.logger { }

public sealed class VoiceEvent {
    public object DeserializationStrategy : KDeserializationStrategy<VoiceEvent?> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
            element("op", OpCode.Serializer.descriptor)
            element("d", JsonElement.serializer().descriptor)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): VoiceEvent? {
            var op: OpCode? = null
            var data: VoiceEvent? = null

            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> break@loop
                        0 -> op = OpCode.Serializer.deserialize(decoder)
                        1 -> data = when (op) {
                            OpCode.Hello -> decodeSerializableElement(
                                descriptor,
                                index,
                                Hello.serializer()
                            )
                            OpCode.HeartbeatAck -> {
                                HeartbeatAck(decodeInlineElement(HeartbeatAck.serializer().descriptor, 0).decodeLong())
                            }
                            OpCode.Ready -> decodeSerializableElement(descriptor, index, Ready.serializer())
                            OpCode.SessionDescription -> decodeSerializableElement(
                                descriptor,
                                index,
                                SessionDescription.serializer()
                            )
                            OpCode.Speaking -> decodeSerializableElement(descriptor, index, Speaking.serializer())
                            OpCode.Resumed -> Resumed
                            else -> {
                                val element = decodeNullableSerializableElement(
                                    descriptor,
                                    index,
                                    JsonElement.serializer().nullable
                                )

                                jsonLogger.debug { "Unknown event with Opcode $op : $element" }
                                null
                            }
                        }
                    }
                }
                endStructure(descriptor)
                return data
            }
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

@Serializable
public data class HeartbeatAck(val nonce: Long) : VoiceEvent()

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

@Serializable
public object Resumed : VoiceEvent()

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
