package dev.kord.common

import dev.kord.common.annotation.KordInternal

@KordInternal
public actual object Platform {
    public actual const val IS_JVM: Boolean = true
    public actual const val IS_NODE: Boolean = false
    public actual const val IS_BROWSER: Boolean = false
}
