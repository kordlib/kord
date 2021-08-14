package dev.kord.voice.gateway

import dev.kord.gateway.Gateway
import dev.kord.gateway.start
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

sealed class Event {
    companion object : DeserializationStrategy<Event?> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
            element("op", OpCode.descriptor)
            element("d", JsonElement.serializer().descriptor)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Event? {
            var op: OpCode? = null
            var data: Event? = null

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
) : Event()

@Serializable
data class Hello(
    @SerialName("v")
    val version: Short,
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Double
) : Event()

@Serializable
data class HeartbeatAck(val nonce: Long) : Event()

@Serializable
data class SessionDescription(
    val mode: EncryptionMode,
    @SerialName("secret_key")
    val secretKey: List<UByte>
) : Event()

@Serializable
object Resumed : Event()

sealed class Close : Event() {
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
     * The gateway closed and will attempt to start a new session.
     */
    object SessionReset : Close()

//    /**
//     * Discord is no longer responding to the gateway commands, the connection will be closed and an attempt to resume the session will be made.
//     * Any [commands][Command] send recently might not complete, and won't be automatically requeued.
//     */
//    object ZombieConnection : Close()

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [Gateway.start], otherwise all resources linked to the Gateway should free and the Gateway [detached][Gateway.detach].
     */
    object RetryLimitReached : Close()
}