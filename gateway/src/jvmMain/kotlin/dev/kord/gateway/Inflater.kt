package dev.kord.gateway

import io.ktor.websocket.*
import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream
import java.util.zip.Inflater as JvmInflater

internal actual class Inflater {
    private val delegate = JvmInflater()

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
