package dev.kord.gateway

import dev.kord.gateway.internal.Decompress
import io.ktor.websocket.*
import js.typedarrays.toUint8Array

internal actual fun ZstdDecompressor() = object : Decompressor {
    private val stream = Decompress()

    override fun Frame.decompress(): String {
        var cache = ByteArray(0)
        stream.onData = { data, _ ->
            cache += data.toByteArray()
        }
        // This call is sync, so if it finishes, the cache will store all the chunks
        stream.push(data.toUint8Array())
        return cache.decodeToString()
    }

    override fun close() = Unit
}
