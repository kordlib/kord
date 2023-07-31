package dev.kord.gateway

import io.ktor.utils.io.core.*
import io.ktor.websocket.*

internal expect class Inflater() : Closeable {
    fun Frame.inflateData(): String
}
