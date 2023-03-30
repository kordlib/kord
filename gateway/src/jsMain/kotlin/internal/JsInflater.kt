@file:JsModule("fast-zlib")
@file:JsNonModule

package dev.kord.gateway.internal

import js.typedarrays.Uint8Array
import node.buffer.Buffer

internal open external class Inflate {
    open fun process(data: Uint8Array): Buffer
}
