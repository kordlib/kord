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
        val sequence: Int? = null,
        @SerialName("t")
        val name: String? = null
) {
    constructor(opCode: OpCode, data: Number, sequence: Int? = null, name: String? = null) : this(opCode, data.primitive(), sequence, name)
    constructor(opCode: OpCode, data: Boolean, sequence: Int? = null, name: String? = null) : this(opCode, data.primitive(), sequence, name)
    constructor(opCode: OpCode, data: String, sequence: Int? = null, name: String? = null) : this(opCode, data.primitive(), sequence, name)

    companion object {
        @ImplicitReflectionSerializer
        inline operator fun <reified T : Event> invoke(opCode: OpCode, data: T, sequence: Int? = null, name: String? = null) =
                Payload(opCode, Json.plain.toJson(T::class.serializer(), data), sequence, name)
    }

}

@ImplicitReflectionSerializer
@UnstableDefault
fun Payload.stringify() = Json.stringify(Payload.serializer(), this)


fun Number?.primitive() = JsonPrimitive(this)
fun String?.primitive() = JsonPrimitive(this)
fun Boolean?.primitive() = JsonPrimitive(this)


