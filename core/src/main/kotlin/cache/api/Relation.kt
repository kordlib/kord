package dev.kord.core.cache.api

public typealias RelationHandler<T, R> = (value: T, friend: R) -> Boolean

/**
 * A `Relation` is a bi-directional link between two entities of type `T` and `R`.
 * A `Relation` is defined by a set of `RelationHandler`s which determine the relationship
 * between two entities.
 *
 * @param T the type of the first entity in the relation.
 */

public interface Relation<T: Any> {

    /**
     * Removes the given [value] from all caches related to this relation.
     *
     * @param value the entity to remove from the relation.
     */
    public fun discard(value: T)

    /**
     * Returns the typed cache associated with this relation.
     *
     * @return the typed cache associated with this relation.
     */
    public fun getCaches(): TypedCache

    /**
     * Associates an [EntryCache] of type `T` with this relation.
     *
     * @param cache the cache to associate with this relation.
     */
    public fun <T: Any> putCache(cache: EntryCache<T>)

    /**
     * Associates a `RelationHandler` with this relation.
     *
     * @param handler the `RelationHandler` to associate with this relation.
     * @param R the type of the second entity in the relation.
     */
    public fun <R: Any> relate(handler: RelationHandler<T, R>)
}
