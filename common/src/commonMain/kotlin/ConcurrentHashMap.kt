package dev.kord.common

import dev.kord.common.annotation.KordInternal

/**
 * Platform agnostic implementation of ConcurrentHashMap.
 */
@KordInternal
public expect class ConcurrentHashMap<K, V>() : MutableMap<K, V>
