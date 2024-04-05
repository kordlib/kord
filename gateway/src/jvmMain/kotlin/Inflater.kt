package dev.kord.gateway

import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream

internal actual fun Inflater() = object : Inflater {
    private val delegate = java.util.zip.Inflater()
    private val buffer = ByteArrayOutputStream()

    override fun inflate(compressed: ByteArray, compressedLen: Int): String {
        buffer.reset()
        InflaterOutputStream(buffer, delegate).use {
            it.write(compressed, /* off = */ 0, /* len = */ compressedLen)
        }
        return buffer.toString("UTF-8")
    }

    override fun close() = delegate.end()
}
