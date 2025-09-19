@file:JsModule("fast-zlib")

package dev.kord.gateway.internal

import js.buffer.ArrayBufferLike
import node.buffer.Buffer

internal external class Inflate {
    fun process(@Suppress("unused") data: Buffer<ArrayBufferLike>): Buffer<ArrayBufferLike>

    fun close()
}
