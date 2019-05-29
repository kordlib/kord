package com.gitlab.hopebaron.websocket

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@Serializable
sealed class Event
sealed class Command

@ImplicitReflectionSerializer
@UnstableDefault
inline fun <reified T : Event> JsonElement.event() = Json.plain.fromJson(T::class.serializer(), this)

@UnstableDefault
@ImplicitReflectionSerializer
inline fun <reified T : Command> JsonElement.command() = Json.plain.fromJson(T::class.serializer(), this)

