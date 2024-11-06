package dev.kord.gateway

import com.github.luben.zstd.ZstdInputStream
import io.ktor.websocket.*
import java.io.ByteArrayInputStream

internal actual fun ZstdDecompressor() = object : Decompressor {

    private val input = UpdatableByteArrayInputStream()
    private val zstdStream = ZstdInputStream(input).apply { continuous = true }

    override fun Frame.decompress(): String {
        input.update(data)
        return zstdStream.readBytes().decodeToString()
    }

    override fun close() {
        zstdStream.close()
    }
}

private class UpdatableByteArrayInputStream : ByteArrayInputStream(ByteArray(0)) {
    fun update(newBuf: ByteArray) {
        this.pos = 0
        this.buf = newBuf
        this.count = newBuf.size
    }
}
