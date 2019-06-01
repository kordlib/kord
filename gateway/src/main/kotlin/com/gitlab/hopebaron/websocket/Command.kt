package com.gitlab.hopebaron.websocket

import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject


sealed class Command

@UnstableDefault
fun <T : Command> JsonObject.command(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)