package dev.kord.core.cache.api

public interface Cache<Value> {

    /**
     * Returns the value associated with [key] in this cache, or null if there is no
     * cached value for [key].
     */
    public fun get(transform: (Value) -> Boolean): Value?
    /**
     * Associates [value] with [key] in this cache. If the cache previously contained a
     * value associated with [key], the old value is replaced by [value].
     */
    public fun put(value: Value)

    /**
     * Discards any cached value for key [key].
     */
    public fun discardIf(transform: (Value) -> Boolean)

    /**
     * Discards all entries in the cache.
     */
    public fun discardAll()

    /**
     * Returns a defensive copy of cache entries as [Map].
     */
    public fun asSet(): Set<Value>

}