package dev.kord.core.cache.api

import io.ktor.util.collections.*

/**
 * An implementation of the [EntryCache] interface that uses an index system based on the [IndexFactory] provided.
 * This implementation is thread-safe.
 *
 * @param indexFactory The factory that generates indices for values stored in this cache.
 * @param relation The relation between this cache and other caches, used to discard related entries.
 */
public class IndexCache<Value : Any>(
    public val indexFactory: IndexFactory<Value>,
    public val relation: Relation<Value>
) : EntryCache<Value> {
    
    private val source: ConcurrentMap<Index<Value>, Value> = ConcurrentMap()
    
    /**
     * Gets the value associated with the given [key].
     *
     * @return The value associated with the given [key], or `null` if not found.
     */
    override fun get(key: Index<Value>): Value? {
        return source[key]
    }

    /**
     * Gets the first value that matches the [transform] function.
     *
     * @return The first value that matches the [transform] function, or `null` if not found.
     */
    override fun get(transform: (Value) -> Boolean): Value? {
        return source.values.singleOrNull(transform)
    }

    /**
     * Discards all values that match the [transform] function.
     *
     * @param transform The function used to determine which values to discard.
     */
    override fun discardIf(transform: (Value) -> Boolean) {
        val value = get(transform)
        if(value != null) source.remove(indexFactory.create(value))
    }

    /**
     * Discards the value associated with the given [index].
     *
     * @return The value associated with the given [index], or `null` if not found.
     */
    override fun discard(index: Index<Value>): Value? {
        return source.remove(index)
    }

    /**
     * Adds the given [value] to the cache and returns its associated [Index].
     *
     * @return The [Index] associated with the added [value].
     */
    override fun put(value: Value): Index<Value> {
        val index = indexFactory.create(value)
        source[index] = value
        return index
    }

    /**
     * Discards all entries from this cache.
     */
    override fun discardAll() {
        source.onEach { relation.discard(it.value) }
        source.clear()
    }

    /**
     * Adds an observer and discards related entries based on [relation].
     *
     * @param cache The cache to observe.
     */
    override fun addObserver(cache: EntryCache<Any>) {
        relation.putCache(cache)
    }

    /**
     * Returns a defensive copy of the underlying [ConcurrentMap].
     *
     * @return A defensive copy of the underlying [ConcurrentMap].
     */
    override fun asMap(): Map<Index<Value>, Value> {
        return source.toMap()
    }

    /**
     * A factory that creates instances of the [IndexCache] class.
     */
    public object Factory: CacheFactory {

        /**
         * Creates a new instance of the [IndexCache] class.
         *
         * @return A new instance of the [IndexCache] class.
         */
        override fun <T : Any> create(): EntryCache<T> {
            val indexFactory: IndexFactory<T> = TODO()
            val relation = BasicRelation()
            return IndexCache(indexFactory, relation)
        }
    }
}
