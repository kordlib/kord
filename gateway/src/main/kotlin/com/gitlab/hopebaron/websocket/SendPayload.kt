package com.gitlab.hopebaron.websocket

import kotlinx.serialization.Serializable

@Serializable
data class SendPayload(
        val opCode: OpCode,
        val data: Command? = null,
        val sequence: Int? = null,
        val name: String? = null)