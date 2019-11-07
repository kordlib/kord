package com.gitlab.kordlib.gateway

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

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

    @Serializer(forClass = OpCode::class)
    companion object OpCodeSerializer : KSerializer<OpCode> {
        override val descriptor: SerialDescriptor
            get() = IntDescriptor.withName("op")

        override fun deserialize(decoder: Decoder): OpCode {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: OpCode) {
            encoder.encodeInt(obj.code)
        }
    }

}