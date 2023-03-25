package dev.kord.common

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.kord.common.annotation.KordInternal

@KordInternal
public actual typealias ConcurrentHashMap<K, V> = ConcurrentMutableMap<K, V>
