package dev.kord.voice.gateway

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OpCode.Serializer::class)
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
    ClientDisconnect(13),
    MediaSinkWants(15);

    internal object Serializer : KSerializer<OpCode> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.voice.gateway.OpCode", PrimitiveKind.INT)

        private val entriesByCode = entries.associateBy { it.code }
        override fun deserialize(decoder: Decoder): OpCode {
            val code = decoder.decodeInt()
            return entriesByCode[code] ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: OpCode) {
            encoder.encodeInt(value.code)
        }
    }
}
