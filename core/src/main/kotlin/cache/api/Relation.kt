package dev.kord.core.cache.api
public typealias RelationHandler<T, R> = (value: T, friend: R) -> Boolean
public interface Relation<T: Any> {
    public fun discard(value: T)
    public fun getCaches(): TypedCache
    public fun <T: Any> putCache(cache: EntryCache<T>)
    public fun <R: Any> relate(handler: RelationHandler<T, R>)
}