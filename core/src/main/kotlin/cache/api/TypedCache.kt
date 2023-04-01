package dev.kord.core.cache.api

public interface TypedCache {
    public fun  <T: Any> getType(): Cache<T>
    public fun <T: Any> putCache(factory: CacheFactory): Cache<T>
}
public interface CacheFactory {
    public fun <T: Any> create(): Cache<T>
}