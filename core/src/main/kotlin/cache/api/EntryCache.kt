package dev.kord.core.cache.api

public interface EntryCache<Value: Any> {

    /**
     * Returns the value associated with [key] in this cache, or null if there is no
     * cached value for [key].
     */

    public fun get(transform: (Value) -> Boolean): Value?
    /**
     * Associates [value] with [key] in this cache. If the cache previously contained a
     * value associated with [key], the old value is replaced by [value].
     */
    public fun put(value: Value): Index<Value>

    /**
     * Discards any cached value for key [key].
     */
    public fun discardIf(transform: (Value) -> Boolean)

    public fun discard(index: Index<Value>): Value?

    /**
     * Discards all entries in the cache.
     */
    public fun discardAll()

    public fun addObserver(cache: EntryCache<Any>)

    /**
     * Returns a defensive copy of cache entries as [Map].
     */
    public fun asMap(): Map<Index<Value>, Value>

    public fun get(key: Index<Value>): Value?
}