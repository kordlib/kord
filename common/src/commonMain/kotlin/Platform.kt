package dev.kord.common

import dev.kord.common.annotation.KordInternal

@KordInternal
public expect object Platform {
    public val IS_JVM: Boolean
    public val IS_NODE: Boolean
    public val IS_BROWSER: Boolean
}
