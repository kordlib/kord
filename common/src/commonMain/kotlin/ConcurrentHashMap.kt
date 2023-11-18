package dev.kord.common

import dev.kord.common.annotation.KordInternal

/**
 * Platform-agnostic implementation of ConcurrentHashMap.
 *
 * @suppress
 */
@KordInternal
public expect fun <K, V> concurrentHashMap(): MutableMap<K, V>
