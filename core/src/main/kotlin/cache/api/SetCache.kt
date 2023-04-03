package dev.kord.core.cache.api

public class SetCache<Value : Any> : EntryCache<Value> {

    private val source: MutableSet<Value> = mutableSetOf()
    private val relation = BasicRelation<Value>()


    override fun get(transform: (Value) -> Boolean): Value? {
        return source.find(transform)
    }

    override fun put(value: Value) {
        source.add(value)
    }

    override fun discardIf(transform: (Value) -> Boolean) {
        val value = get(transform)
        if(value != null) relation.discard(value)
        source.removeAll(transform)
    }

    override fun discardAll() {
        source.clear()
    }

    override fun addObserver(cache: EntryCache<Any>) {
        relation.putCache(cache)
    }

    override fun asSet(): Set<Value> {
        return source.toSet()
    }

    public object Factory: CacheFactory {
        override fun <T : Any> create(): EntryCache<T> {
            return SetCache()
        }
    }

}
