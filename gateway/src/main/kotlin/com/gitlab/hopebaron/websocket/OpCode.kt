package com.gitlab.hopebaron.websocket

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
enum class OpCode(val code: Int) {
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
    HelloACK(11);

companion object : KSerializer<OpCode> {
    override val descriptor: SerialDescriptor
        get() = IntDescriptor.withName("op")

    override fun deserialize(decoder: Decoder): OpCode {
        val code = decoder.decodeInt()
        return values().first { it.code == code }
    }

    override fun serialize(encoder: Encoder, obj: OpCode) {
        encoder.encodeInt(obj.code)
    }
}
}
