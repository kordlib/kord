package dev.kord.gateway

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

internal class Sequence {
    private val counter: AtomicRef<Int?> = atomic(null)

    var value
        get() = counter.value
        set(value) = counter.update { value }
}