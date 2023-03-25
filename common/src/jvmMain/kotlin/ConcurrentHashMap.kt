package dev.kord.common

import dev.kord.common.annotation.KordInternal
import java.util.concurrent.ConcurrentHashMap as JavaConcurrentHashMap

@KordInternal
public actual typealias ConcurrentHashMap<K, V> = JavaConcurrentHashMap<K, V>
