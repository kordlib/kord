package dev.kord.gateway

import io.ktor.websocket.*
import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream

internal actual class Inflater {
    private val delegate = java.util.zip.Inflater()

    actual suspend fun Frame.inflateData(): String {
        val outputStream = ByteArrayOutputStream()
        InflaterOutputStream(outputStream, delegate).use {
            it.write(data)
        }

        return outputStream.use {
            String(outputStream.toByteArray(), 0, outputStream.size(), Charsets.UTF_8)
        }
    }
}
