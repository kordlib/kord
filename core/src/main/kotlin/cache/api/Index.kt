package dev.kord.core.cache.api

/**
 * A unique identifier for a cached value of type [T].
 * It implements [Comparable] to allow for indexing and sorting.
 */
public interface Index<T: Any>: Comparable<Index<T>>

/**
 * A factory for creating [Index] instances for objects of type [T].
 * Implementations are responsible for generating unique indices for objects.
 */
public interface IndexFactory<T: Any> {

    /**
     * Creates an [Index] instance for the given [entry].
     * The returned [Index] should be unique for the given [entry].
     */
    public fun create(entry: T): Index<T>
}
