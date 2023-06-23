package dev.kord.gateway

import io.ktor.websocket.*

internal expect class Inflater() {
    fun Frame.inflateData(): String
}
