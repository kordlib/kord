package dev.kord.core.cache.api

/**
 * An interface representing a cache that can store multiple types of data by associating them with their types.
 */
public interface TypedCache {

    /**
     * Returns the [EntryCache] associated with the type [T].
     */
    public fun <T : Any> getType(): EntryCache<T>

    /**
     * Creates and returns a new [EntryCache] for type [T] using the given [CacheFactory] and adds it to this [TypedCache].
     */
    public fun <T : Any> putCache(factory: CacheFactory): EntryCache<T>

    /**
     * Adds the given [EntryCache] to this [TypedCache].
     */
    public fun <T : Any> putCache(cache: EntryCache<T>)

    /**
     * Returns a [Set] of all the [EntryCache]s stored in this [TypedCache].
     */
    public fun toSet(): Set<EntryCache<Any>>
}

/**
 * An interface representing a factory that creates [EntryCache]s.
 */
public interface CacheFactory {

    /**
     * Creates and returns a new [EntryCache] for type [T] using the given [IndexFactory] and [Relation].
     */
    public fun <T : Any> create(): EntryCache<T>
}
