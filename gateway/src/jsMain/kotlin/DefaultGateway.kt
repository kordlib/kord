package dev.kord.gateway

import io.ktor.client.plugins.websocket.*

internal actual fun Throwable.isTimeout() = this is WebSocketException && "ENOTFOUND" in toString()
internal actual val os: String get() = js("process.platform") as String
