package com.gitlab.hopebaron.websocket

enum class OpCode(val code: Int) {
    Dispatch(0),
    Heartbeat(1),
    Identify(2),
    Status_Update(3),
    Voice_State_Update(4),
    Resume(6),
    Reconnect(7),
    Request_Guild_Memebers(8),
    Invalid_Session(9),
    Hello(10),
    Hello_ACK(11)


}
