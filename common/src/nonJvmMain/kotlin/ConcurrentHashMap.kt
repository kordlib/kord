package dev.kord.common

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.kord.common.annotation.KordInternal

/** @suppress */
@KordInternal
// using an actual typealias seems to be broken in Kotlin/JS 1.9.0
// public actual typealias ConcurrentHashMap<K, V> = ConcurrentMutableMap<K, V>
public actual class ConcurrentHashMap<K, V> : MutableMap<K, V> by ConcurrentMutableMap()
