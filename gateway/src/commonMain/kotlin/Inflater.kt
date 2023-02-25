package dev.kord.gateway

import io.ktor.websocket.*

internal expect class Inflater() {
    suspend fun Frame.inflateData(): String
}
