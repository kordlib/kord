package dev.kord.common

import dev.kord.common.annotation.KordInternal
import java.util.concurrent.ConcurrentHashMap

/** @suppress */
@KordInternal
public actual fun <K, V> concurrentHashMap(): MutableMap<K, V> = ConcurrentHashMap()
