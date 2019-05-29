package com.gitlab.hopebaron.websocket

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@Serializable
sealed class Event

@ImplicitReflectionSerializer
@UnstableDefault
inline fun <reified T : Event> JsonElement.event() = Json.plain.fromJson(T::class.serializer(), this)

@ImplicitReflectionSerializer
inline fun <reified T : Event> JsonElement.event(expected: KClass<T>) = Json.plain.fromJson(expected.serializer(), this)


@Serializable
data class HelloEvent(
        @SerialName("heartbeat_interval")
        val interval: Long,
        @SerialName("_trace")
        val trace: String) : Event()

@Serializable
data class ResumeEvent(val token: String,
                       @SerialName("session_id")
                       val id: String,
                       @SerialName("seq")
                       val sequance: Int) : Event()


