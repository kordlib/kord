package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.DataEntryCache
import com.gitlab.kordlib.cache.api.Query
import com.gitlab.kordlib.cache.api.QueryBuilder
import com.gitlab.kordlib.cache.api.data.DataDescription
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

class ViewKeys(private val keySet: MutableSet<Any> = mutableSetOf()) {
    val keys: Set<Any> = keySet

    fun add(key: Any) {
        keySet.add(key)
    }
}

/**
 * A [DataCacheView] that limits removal of cached instances to those inserted in this cache,
 * and not the underlying [cache].
 */
class DataCacheView(private val cache: DataCache) : DataCache by cache {
    private val keys = ViewKeys()
    private val descriptions = mutableMapOf<KType, DataDescription<out Any, out Any>>()

    @Suppress("UNCHECKED_CAST")
    private fun<T: Any> getDescription(type: KType) = descriptions[type]!! as DataDescription<T, out Any>

    override suspend fun register(description: DataDescription<out Any, out Any>) {
        descriptions[description.type] = description
    }

    override suspend fun register(vararg descriptions: DataDescription<out Any, out Any>) {
        descriptions.forEach { register(it) }
    }

    override suspend fun register(descriptions: Iterable<DataDescription<out Any, out Any>>) {
        descriptions.forEach { register(it) }
    }

    override fun <T : Any> getEntry(type: KType): DataEntryCache<T>? {
        return cache.getEntry<T>(type)?.let { DataEntryCacheView(it, getDescription(type), keys) }
    }

}

private class DataEntryCacheView<T : Any>(
        private val entryCache: DataEntryCache<T>,
        private val description: DataDescription<T, out Any>,
        private val viewKeys: ViewKeys
) : DataEntryCache<T> by entryCache {

    override suspend fun put(item: T) {
        entryCache.put(item)
        viewKeys.add(description.indexField.property.get(item))
    }

    override fun query(): QueryBuilder<T> {
        return QueryBuilderView(entryCache.query(), description.indexField.property, viewKeys.keys)
    }

}

private class QueryBuilderView<T : Any>(
        private val builder: QueryBuilder<T>,
        private val property: KProperty1<T, Any>,
        private val keys: Set<Any>
) : QueryBuilder<T> by builder {
    override fun build(): Query<T> = QueryView(builder, property, keys)
}

private class QueryView<T : Any>(
        private val builder: QueryBuilder<T>,
        private val property: KProperty1<T, Any>,
        private val keys: Set<Any>
) : Query<T> by builder.build() {
    override suspend fun remove() = builder.apply { property `in` keys }.build().remove()
}