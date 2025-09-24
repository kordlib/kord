@file:JsModule("fast-zlib")

package dev.kord.gateway.internal

import js.buffer.ArrayBuffer
import js.buffer.ArrayBufferLike
import node.buffer.Buffer

internal external class Inflate {
    fun process(@Suppress("unused") data: Buffer<ArrayBuffer>): Buffer<ArrayBufferLike>

    fun close()
}
