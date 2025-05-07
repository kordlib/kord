package dev.kord.gateway

import io.ktor.websocket.*
import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream

internal actual fun ZLibDecompressor() = object : Decompressor {
    private val delegate = java.util.zip.Inflater()

    override fun Frame.decompress(): String {
        val outputStream = ByteArrayOutputStream()
        InflaterOutputStream(outputStream, delegate).use {
            it.write(data)
        }

        return outputStream.use { it.toByteArray().decodeToString() }
    }

    override fun close() = delegate.end()
}
