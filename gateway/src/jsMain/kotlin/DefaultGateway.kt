package dev.kord.gateway

import io.ktor.client.plugins.websocket.*
import node.process.process

internal actual fun Throwable.isTimeout() = this is WebSocketException && "ENOTFOUND" in toString()
internal actual val os: String get() = process.platform.toString()
