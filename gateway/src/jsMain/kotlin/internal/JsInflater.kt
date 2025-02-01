@file:JsModule("fast-zlib")

package dev.kord.gateway.internal

import js.buffer.ArrayBufferLike
import js.typedarrays.Uint8Array
import node.buffer.Buffer

internal external class Inflate {
    fun process(data: Uint8Array<ArrayBufferLike>): Buffer<ArrayBufferLike>

    fun close()
}
