package dev.kord.core.cache.api

import io.ktor.util.collections.*

public class IndexCache<Value : Any>(
    public val indexFactory: IndexFactory<Value>,
    public val relation: Relation<Value>
) : EntryCache<Value> {
    private val source: ConcurrentMap<Index<Value>, Value> = ConcurrentMap()
    public override fun get(key: Index<Value>): Value? {
        return source[key]
    }

    override fun get(transform: (Value) -> Boolean): Value? {
        return source.values.singleOrNull(transform)
    }
    override fun discardIf(transform: (Value) -> Boolean) {
        val value = get(transform)
        if(value != null) source.remove(indexFactory.create(value))
    }

    public override fun discard(index: Index<Value>): Value? {
        return source.remove(index)
    }

    override fun put(value: Value): Index<Value> {
        val index = indexFactory.create(value)
        source[index] = value
        return index
    }

    override fun discardAll() {
        source.clear()
    }

    override fun addObserver(cache: EntryCache<Any>) {
        relation.putCache(cache)
    }

    override fun asMap(): Map<Index<Value>, Value> {
        return source.toMap()
    }

    public object Factory: CacheFactory {
        override fun <T : Any> create(indexFactory: IndexFactory<T>, relation: Relation<T>): EntryCache<T> {
            return IndexCache(indexFactory, relation)
        }
    }

}
