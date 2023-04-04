package dev.kord.core.cache.api

/**
 * A unique identifier for a cached value.
 * It implements [Comparable] to allow for indexing and sorting.
 */
public interface Index : Comparable<Index> {

    /**
     * Computes and returns the hash code for this index.
     */
    override fun hashCode(): Int

}
