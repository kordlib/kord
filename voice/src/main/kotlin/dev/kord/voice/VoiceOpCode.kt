package dev.kord.voice

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = VoiceOpCode.VoiceOpCodeSerializer::class)
enum class VoiceOpCode(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    Identify(0),
    SelectProtocol(1),
    Ready(2),
    Heartbeat(3),
    SessionDescription(4),
    Speaking(5),
    HeartbeatACK(6),
    Resume(7),
    Hello(8),
    Resumed(9),
    ClientDisconnect(13);

    companion object VoiceOpCodeSerializer : KSerializer<VoiceOpCode> {
        fun of(value: Int) = values().firstOrNull { it.code == value } ?: Unknown

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("voiceO" +
                    "p", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): VoiceOpCode {
            val code = decoder.decodeInt()
            return of(code)
        }

        override fun serialize(encoder: Encoder, value: VoiceOpCode) {
            encoder.encodeInt(value.code)
        }
    }

}