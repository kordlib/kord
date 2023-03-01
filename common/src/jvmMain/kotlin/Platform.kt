package dev.kord.common

public actual object Platform {
    public actual val IS_JVM: Boolean = true
    public actual val IS_NODE: Boolean = false
    public actual val IS_BROWSER: Boolean = false
}
