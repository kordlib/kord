package dev.kord.common

import dev.kord.common.annotation.KordInternal

@KordInternal
public actual object Platform {
    public actual val IS_JVM: Boolean = false
    public actual val IS_NODE: Boolean
        get() = js(
            "typeof process !== 'undefined' && process.versions != null && process.versions.node != null"
        ) as Boolean
    public actual val IS_BROWSER: Boolean
        get() = js(
            "typeof window !== 'undefined' && typeof window.document !== 'undefined' || typeof self !== 'undefined' && typeof self.location !== 'undefined'"
        ) as Boolean
}
