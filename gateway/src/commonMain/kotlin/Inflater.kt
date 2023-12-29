package dev.kord.gateway

import io.ktor.utils.io.core.*
import io.ktor.websocket.*

internal interface Inflater : Closeable {
    fun Frame.inflateData(): String
}

internal expect fun Inflater(): Inflater
