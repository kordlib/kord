package dev.kord.core.cache.api

/**
 * A [TypedCache] implementation that stores a set of [EntryCache]s.
 */
public class TypedSetCache : TypedCache {

    /**
     * The set of [EntryCache]s stored in this [TypedSetCache].
     */
    private val types: MutableSet<EntryCache<Any>> = mutableSetOf()

    /**
     * Returns the [EntryCache] for the specified type, or `null` if it is not found.
     */
    private fun <T : Any> getTypeOrNull(): EntryCache<T>? {
        return toSet().filterIsInstance<EntryCache<T>>().singleOrNull()
    }

    /**
     * Returns the [EntryCache] for the specified type, throwing an exception if it is not found.
     */
    override fun <T : Any> getType(): EntryCache<T> {
        val instance = getTypeOrNull<T>()
        require(instance != null) { "Cache for type T not found" }
        return instance
    }

    /**
     * Adds the specified [EntryCache] to the [TypedSetCache].
     * @throws IllegalArgumentException if a cache for the same type already exists.
     */
    override fun <T : Any> putCache(cache: EntryCache<T>) {
        require(getTypeOrNull<T>() == null) { "There must be only one cache of the same type" }
        types.add(cache as EntryCache<Any>)
    }

    /**
     * Returns a set of all [EntryCache]s stored in this [TypedSetCache].
     */
    public override fun toSet(): Set<EntryCache<Any>> = types
}
