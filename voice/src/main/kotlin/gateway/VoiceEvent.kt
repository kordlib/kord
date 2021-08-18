@file:OptIn(KordVoice::class)

package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.EncryptionMode
import kotlinx.serialization.DeserializationStrategy
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

private val jsonLogger = KotlinLogging.logger { }

sealed class VoiceEvent {
    companion object : DeserializationStrategy<VoiceEvent?> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
            element("op", OpCode.descriptor)
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
                        0 -> op = OpCode.deserialize(decoder)
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
data class Ready(
    val ssrc: Int,
    val ip: String,
    val port: Int,
    val modes: List<EncryptionMode>
) : VoiceEvent()

@Serializable
data class Hello(
    @SerialName("v")
    val version: Short,
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Double
) : VoiceEvent()

@Serializable
data class HeartbeatAck(val nonce: Long) : VoiceEvent()

@Serializable
data class SessionDescription(
    val mode: EncryptionMode,
    @SerialName("secret_key")
    val secretKey: List<UByte>
) : VoiceEvent()

@Serializable
object Resumed : VoiceEvent()

sealed class Close : VoiceEvent() {
    /**
     * The user closed the Gateway connection.
     */
    object UserClose : Close()

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    object Timeout : Close()

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    class DiscordClose(val closeCode: VoiceGatewayCloseCode, val recoverable: Boolean) : Close()

    /**
     * The gateway closed and will attempt to resume the session.
     */
    object Reconnecting : Close()

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [VoiceGateway.start], otherwise all resources linked to the Gateway should free.
     */
    object RetryLimitReached : Close()
}