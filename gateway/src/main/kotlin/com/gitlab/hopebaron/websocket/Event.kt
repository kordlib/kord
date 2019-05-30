package com.gitlab.hopebaron.websocket

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectSerializer
import kotlin.reflect.KClass

@Serializable
sealed class Event

sealed class Command

@UnstableDefault
fun <T : Event> JsonObject.event(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

@UnstableDefault
fun <T : Command> JsonObject.command(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

