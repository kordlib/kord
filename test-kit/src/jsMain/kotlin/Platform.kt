package dev.kord.test

import node.process.process

actual object Platform {
    actual const val IS_JVM: Boolean = false
    actual val IS_NODE: Boolean
        get() = js(
            "typeof process !== 'undefined' && process.versions != null && process.versions.node != null"
        ) as Boolean
    actual val IS_BROWSER: Boolean
        get() = js(
            "typeof window !== 'undefined' && typeof window.document !== 'undefined' || typeof self !== 'undefined' && typeof self.location !== 'undefined'"
        ) as Boolean
    actual const val IS_MINGW: Boolean = false
    actual const val IS_LINUX: Boolean = false
    actual const val IS_DARWIN: Boolean = false
}

actual fun getEnv(name: String) = process.env[name]
