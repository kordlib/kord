package dev.kord.common

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.kord.common.annotation.KordInternal

/** @suppress */
@KordInternal
public actual fun <K, V> concurrentHashMap(): MutableMap<K, V> = ConcurrentMutableMap()
