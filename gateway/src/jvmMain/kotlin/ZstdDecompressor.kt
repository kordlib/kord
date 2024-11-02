package dev.kord.gateway

import com.github.luben.zstd.ZstdInputStream
import io.ktor.websocket.*
import java.io.ByteArrayInputStream
import java.io.InputStream

internal actual fun ZstdDecompressor() = object : Decompressor {

    private val input = UpdatableByteArrayInputStream()
    private val zstdStream = ZstdInputStream(input).apply { continuous = true }

    override fun Frame.decompress(): String {
        input.updateDelegate(data)
        return zstdStream.readAllBytes().decodeToString()
    }

    override fun close() {
        zstdStream.close()
    }
}

private class UpdatableByteArrayInputStream : InputStream() {
    private var delegate: ByteArrayInputStream? = null

    private val d: InputStream get() = delegate ?: error("No data available")

    override fun read(): Int = d.read()

    fun updateDelegate(bytes: ByteArray) {
        delegate = ByteArrayInputStream(bytes)
    }
}
