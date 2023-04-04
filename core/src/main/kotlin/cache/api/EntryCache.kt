package dev.kord.core.cache.api
/**
 * A cache that associates a [Value] with an [Index].
 */
public interface EntryCache<Value : Any> {

    /**
     * Returns the value associated with the [key] [Index] in this cache, or null if there is no
     * cached value for [key].
     */
    public fun get(key: Index): Value?

    /**
     * Returns the first [Value] that satisfies the given [transform] function, or null if none is found.
     */
    public fun get(transform: (Value) -> Boolean): Value?

    /**
     * Associates the given [value] with a new [Index] in this cache. If the cache previously contained a
     * value associated with the same [Index], the old value is replaced by [value].
     * Returns the newly generated [Index] for [value].
     */
    public fun put(value: Value): Index

    /**
     * Discards any cached [Value] that satisfies the given [transform] function.
     */
    public fun discardIf(transform: (Value) -> Boolean)

    /**
     * Discards the cached [Value] associated with the given [Index], if it exists.
     * Returns the discarded [Value], or null if it wasn't found.
     */
    public fun discard(index: Index): Value?

    /**
     * Discards all entries in the cache.
     */
    public fun discardAll()

    /**
     * Adds an observer cache to this cache. Whenever a value is discarded from this cache, the
     * observer cache will also discard any values that are related to it.
     */
    public fun addObserver(cache: EntryCache<Any>)

    /**
     * Returns a defensive copy of the cache entries as a [Map] of [Index] to [Value].
     */
    public fun asMap(): Map<Index, Value>
}
