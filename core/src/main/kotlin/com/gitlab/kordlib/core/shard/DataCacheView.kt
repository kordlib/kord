package com.gitlab.kordlib.core.shard

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.QueryBuilder
import com.gitlab.kordlib.cache.api.data.DataDescription
import com.gitlab.kordlib.cache.api.query.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * A [DataCacheView] that limits removal of cached instances to those inserted in this cache,
 * and not the underlying [cache].
 */
class DataCacheView(private val cache: DataCache) : DataCache by cache {
    private val keys = mutableSetOf<Any>()
    private val descriptions = mutableMapOf<KClass<out Any>, DataDescription<out Any, out Any>>()

    override suspend fun register(description: DataDescription<out Any, out Any>) {
        super.register(description)
        descriptions[description.clazz] = description
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> put(item: T) {
        cache.put(item)
        val description = descriptions[item::class]!!
        val property = description.indexField.property as KProperty1<T, Any>
        keys += property.get(item)
    }

    @ExperimentalCoroutinesApi
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> query(clazz: KClass<T>): QueryBuilder<T> {
        val query = cache.query(clazz)
        val description = descriptions[clazz]!!
        val property = description.indexField.property as KProperty1<T, Any>
        return QueryBuilderView(query, keys, property)
    }

}

@ExperimentalCoroutinesApi
private class QueryBuilderView<T : Any>(
        private val builder: QueryBuilder<T>,
        private val keys: MutableSet<Any>,
        private val property: KProperty1<T, Any>
) : QueryBuilder<T> by builder {
    override fun build(): Query<T> = QueryView(builder, keys, property)
}

@ExperimentalCoroutinesApi
private class QueryView<T : Any>(
        private val builder: QueryBuilder<T>,
        private val keys: MutableSet<Any>,
        private val property: KProperty1<T, Any>
) : Query<T> by builder.build() {
    override suspend fun remove() = builder.apply { property `in` keys }.build().remove()
}