package dev.kord.core.cache.api

/**
 * A unique identifier for a cached value of type [T].
 * It implements [Comparable] to allow for indexing and sorting.
 */
public interface Index: Comparable<Index>
