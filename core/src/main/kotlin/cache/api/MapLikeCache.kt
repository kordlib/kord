package dev.kord.core.cache.api

public interface MapLikeCache<in Key: Comparable<Key>, Value: Any>  {

    public fun put(key: Key, value: Value)

    public fun get(key: Key): Value?

    public fun discard(key: Key)
    /**
     * Discards all entries in the cache.
     */
    public fun discardAll()

    /**
     * Returns a defensive copy of cache entries as [Map].
     */
    public fun asMap(): Map<in Key, Value>

    public fun get(keys: Set<Key>): Map<in Key, Value>
}