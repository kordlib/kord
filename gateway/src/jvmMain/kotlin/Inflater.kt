package dev.kord.gateway

import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream
import kotlin.io.use

internal actual class Inflater : Closeable {
    private val delegate = java.util.zip.Inflater()

    actual fun Frame.inflateData(): String {
        val outputStream = ByteArrayOutputStream()
        InflaterOutputStream(outputStream, delegate).use {
            it.write(data)
        }

        return outputStream.use { it.toByteArray().decodeToString() }
    }

    override fun close() = delegate.end()
}
