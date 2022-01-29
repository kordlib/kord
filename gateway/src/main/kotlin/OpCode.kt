package dev.kord.gateway

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OpCode.OpCodeSerializer::class)
public enum class OpCode(public val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),

    /**
     * An event was dispatched.
     */
    Dispatch(0),

    /**
     * Fired periodically by the client to keep the connection alive.
     */
    Heartbeat(1),

    /**
     * Starts a new session during the initial handshake.
     */
    Identify(2),

    /**
     * Update the client's presence.
     */
    StatusUpdate(3),

    /**
     * Used to join/leave or move between voice channels.
     */
    VoiceStateUpdate(4),

    /**
     * You should attempt to reconnect and resume immediately.
     */
    Resume(6),

    /**
     * You should attempt to reconnect and resume immediately.
     */
    Reconnect(7),

    /**
     * Request information about offline guild members in a large guild.
     */
    RequestGuildMembers(8),

    /**
     * The session has been invalidated. You should reconnect and identify/resume accordingly.
     */
    InvalidSession(9),

    /**
     * Sent immediately after connecting, contains the `heartbeat_interval` to use.
     */
    Hello(10),

    /**
     * Sent in response to receiving a heartbeat to acknowledge that it has been received.
     */
    HeartbeatACK(11);

    public companion object OpCodeSerializer : KSerializer<OpCode> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("op", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): OpCode {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: OpCode) {
            encoder.encodeInt(value.code)
        }
    }

}
