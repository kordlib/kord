package dev.kord.gateway

import io.ktor.websocket.*

internal interface Inflater : AutoCloseable {
    fun Frame.inflateData(): String
}

internal expect fun Inflater(): Inflater
