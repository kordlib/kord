package dev.kord.voice.gateway

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public enum class OpCode(public val code: Int) {
    Unknown(Int.MIN_VALUE),
    Identify(0),
    SelectProtocol(1),
    Ready(2),
    Heartbeat(3),
    SessionDescription(4),
    Speaking(5),
    HeartbeatAck(6),
    Resume(7),
    Hello(8),
    Resumed(9),
    ClientDisconnect(13);

    internal object Serializer : KSerializer<OpCode> {
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
