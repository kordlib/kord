package dev.kord.core.cache

import dev.kord.cache.api.DataEntryCache
import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.map.lruLinkedHashMap

/**
 * A Generator creating [DataEntryCaches][DataEntryCache] with a maximum [size], removing items on last insertion.
 * Shortcut for [lruLinkedHashMap].
 */
@Suppress("UnusedReceiverParameter") // used for scoping
public fun <T : Any, I : Any> KordCacheBuilder.lruCache(size: Int = 100): Generator<T, I> = { cache, description ->
    MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(size))
}
