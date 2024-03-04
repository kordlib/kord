@file:JsModule("fast-zlib")

package dev.kord.gateway.internal

import js.typedarrays.Uint8Array
import node.buffer.Buffer

internal external class Inflate {
    fun process(data: Uint8Array): Buffer

    fun close()
}
