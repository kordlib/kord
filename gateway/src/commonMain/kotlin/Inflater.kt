package dev.kord.gateway

import io.ktor.utils.io.core.*
import io.ktor.websocket.*

internal interface Inflater : Closeable {
    /**
     * Inflates this frame.
     *
     * @return the inflated frame or null if the received frame was incomplete
     */
    fun Frame.inflateData(): String?
}

internal expect fun Inflater(): Inflater
