package dev.kord.core.cache.api

public interface TypedCache {
    public fun  <T: Any> getType(): EntryCache<T>
    public fun <T: Any> putCache(factory: CacheFactory): EntryCache<T>
    public fun <T: Any> putCache(cache: EntryCache<T>)

    public fun toSet(): Set<EntryCache<Any>>

}
public interface CacheFactory {
    public fun <T: Any> create(): EntryCache<T>

}