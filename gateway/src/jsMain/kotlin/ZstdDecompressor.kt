package dev.kord.gateway

import dev.kord.gateway.internal.ZSTDDecompress
import io.ktor.websocket.*
import js.typedarrays.toUint8Array
import node.stream.DuplexEvent
import web.encoding.TextDecoder

internal actual fun ZstdDecompressor() = object : Decompressor {
    private val stream = ZSTDDecompress()
    private val decoder = TextDecoder()

    override fun Frame.decompress(): String {
        try {
            stream.write(data.toUint8Array())
            stream.on(DuplexEvent.FINISH) {
                println("finish")
            }
            stream.on(DuplexEvent.DATA) {
                println("Data: $it")
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return ""
    }

    override fun close() = stream.end()
}
