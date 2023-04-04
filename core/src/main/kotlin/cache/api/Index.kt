package dev.kord.core.cache.api

public interface Index<T: Any>: Comparable<Index<T>>

public interface IndexFactory<T: Any> {
    public fun create(entry: T): Index<T>
}