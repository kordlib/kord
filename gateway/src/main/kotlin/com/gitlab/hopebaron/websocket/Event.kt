package com.gitlab.hopebaron.websocket

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@Serializable
sealed class Event

sealed class Command

@UnstableDefault
fun <T : Event> JsonElement.event(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

@UnstableDefault
fun <T : Command> JsonElement.command(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

