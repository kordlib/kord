@file:JsModule("fzstd")

package dev.kord.gateway.internal

import js.typedarrays.Uint8Array

internal external class Decompress {
    @JsName("ondata")
    var onData: (data: Uint8Array, final: Boolean) -> Unit
    fun push(chunk: Uint8Array, final: Boolean = definedExternally)
}
