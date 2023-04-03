package dev.kord.core.cache.api

public class TypedSetCache: TypedCache {
    private val types: MutableSet<EntryCache<Any>> = mutableSetOf()

    private fun <T : Any> getTypeOrNull(): EntryCache<T>? {
        return toSet().filterIsInstance<EntryCache<T>>().singleOrNull()
    }

    override fun <T : Any> getType(): EntryCache<T> {
        val instance = getTypeOrNull<T>()
        require(instance != null)
        return instance
    }

    override fun <T : Any> putCache(factory: CacheFactory): EntryCache<T> {
        require(getTypeOrNull<T>() == null) { "There must be only one cache of the same type" }
        val instance = factory.create<T>()
        @Suppress("UNCHECKED_CAST")
        types.add(instance as EntryCache<Any>)
        return instance
    }

    override fun <T : Any> putCache(cache: EntryCache<T>) {
        require(getTypeOrNull<T>() == null) { "There must be only one cache of the same type" }
        types.add(cache as EntryCache<Any>)
    }

    public override fun toSet(): Set<EntryCache<Any>> = types
}