package dev.kord.gateway

import dev.kord.gateway.internal.Inflate
import node.buffer.Buffer
import node.buffer.BufferEncoding

internal actual fun Inflater() = object : Inflater {
    private val inflate = Inflate()

    override fun inflate(compressed: ByteArray, compressedLen: Int): String {
        val buffer = Buffer.from(compressed, byteOffset = 0, length = compressedLen)

        return inflate.process(buffer).toString(BufferEncoding.utf8)
    }

    override fun close() = inflate.close()
}
