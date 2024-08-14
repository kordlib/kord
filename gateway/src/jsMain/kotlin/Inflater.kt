package dev.kord.gateway

import dev.kord.gateway.internal.Inflate
import io.ktor.websocket.*
import node.buffer.Buffer
import node.buffer.BufferEncoding

internal actual fun Inflater() = object : Inflater {
    private val inflate = Inflate()

    override fun Frame.inflateData(): String {
        val buffer = Buffer.from(data)

        return inflate.process(buffer).toString(BufferEncoding.utf8)
    }

    override fun close() = inflate.close()
}
