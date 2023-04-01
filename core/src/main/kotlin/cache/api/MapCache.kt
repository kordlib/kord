package dev.kord.core.cache.api

public class MapCache<Key: Comparable<Key>, Value: Any>(source: Map<Key, Value> = mutableMapOf()): MapLikeCache<Key, Value> {
    private val source = source.toMutableMap()

    override fun get(key: Key): Value? {
        return source[key]
    }

    override fun put(key: Key, value: Value) {
        source[key] = value
    }

    override fun get(keys: Set<Key>): Map<Key, Value> {
        return source.filterKeys { it in keys }
    }

    override fun discard(key: Key) {
        source.remove(key)
    }

    override fun discardAll() {
        source.clear()
    }

    override fun asMap(): Map<in Key, Value> {
        return source.toMap()
    }

}