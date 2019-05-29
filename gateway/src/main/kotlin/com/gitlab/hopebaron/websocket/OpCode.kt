package com.gitlab.hopebaron.websocket

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
    HelloACK(11)


}
