package dev.kord.gateway

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OpCode.OpCodeSerializer::class)
enum class OpCode(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    Dispatch(0),
    Heartbeat(1),
    Identify(2),
    StatusUpdate(3),
    VoiceStateUpdate(4),
    Resume(6),
    Reconnect(7),
    RequestGuildMembers(8),
    InvalidSession(9),
    Hello(10),
    HeartbeatACK(11);

    companion object OpCodeSerializer : KSerializer<OpCode> {
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