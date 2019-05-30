package com.gitlab.hopebaron.websocket

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class Payload(
        @SerialName("op")
        val opCode: OpCode,
        @SerialName("d")
        val data: JsonElement? = null,
        @SerialName("s")
        val sequence: Int? = 0,
        @SerialName("t")
        val name: String? = ""
) {
    constructor(opCode: OpCode, data: Number?, sequence: Int? = null, name: String? = null) : this(opCode, data.primitive(), sequence, name)
    constructor(opCode: OpCode, data: Boolean?, sequence: Int? = null, name: String? = null) : this(opCode, data.primitive(), sequence, name)
    constructor(opCode: OpCode, data: String?, sequence: Int? = null, name: String? = null) : this(opCode, data.primitive(), sequence, name)

    companion object {
        @UnstableDefault
        @ImplicitReflectionSerializer
        inline operator fun <reified T : Command> invoke(opCode: OpCode, data: T, sequence: Int? = null, name: String? = null) =
                Payload(opCode, Json.plain.toJson(T::class.serializer(), data), sequence, name)
    }

}





private fun Number?.primitive() = JsonPrimitive(this)
private fun String?.primitive() = JsonPrimitive(this)
private fun Boolean?.primitive() = JsonPrimitive(this)


