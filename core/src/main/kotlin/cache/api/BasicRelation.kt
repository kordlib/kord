package dev.kord.core.cache.api

/**
 * A basic implementation of [Relation] that maintains a set of relationships and caches.
 * This implementation is thread-safe.
 *
 * @param T the type of the related values.
 */
public class BasicRelation<T : Any> : Relation<T> {

    private val relationships: MutableSet<RelationHandler<T, Any>> = mutableSetOf()

    private val caches: TypedCache = TypedSetCache()

    /**
     * Discards all values related to [value] using the defined [relationships] and [caches].
     */
    override fun discard(value: T) {
        relationships.onEach { relate ->
            getCaches().toSet().onEach { otherCache ->
                otherCache.discardIf { relate(value, it) }
            }
        }
    }

    /**
     * Gets the [TypedCache] containing the cached values.
     */
    override fun getCaches(): TypedCache {
        return caches
    }

    /**
     * Adds an [EntryCache] to the [caches].
     *
     * @param cache the cache to add.
     * @param R the type of the related values.
     */
    override fun <R : Any> to(cache: EntryCache<R>, relation: RelationHandler<T, R>) {
        caches.putCache(cache)
        relationships.add(safe(relation))
    }

    /**
     * Converts a [RelationHandler] of type [R] to [RelationHandler] of type [Any].
     * The new one only applies relate function to type [R].
     *
     * @param handler the handler to convert.
     * @param R the type of the related values.
     */
    private fun <R : Any> safe(handler: RelationHandler<T, R>): RelationHandler<T, Any> {
        return object : RelationHandler<T, Any> {
            override fun invoke(value: T, friend: Any): Boolean {
                @Suppress("UNCHECKED_CAST")
                val safeFriend = friend as? R ?: return false
                return handler(value, safeFriend)
            }
        }
    }
}
