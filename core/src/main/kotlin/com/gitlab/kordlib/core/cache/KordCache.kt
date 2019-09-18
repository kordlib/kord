package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.QueryBuilder
import com.gitlab.kordlib.cache.api.data.DataDescription
import java.lang.IllegalStateException
import kotlin.reflect.KClass

internal class KordCache(private val generator: (DataDescription<*, *>) -> DataCache) : DataCache {
    private val caches = mutableMapOf<KClass<*>, DataCache>()

    override val priority: Long
        get() = Long.MAX_VALUE

    private fun getCacheOrThrow(clazz: KClass<*>) : DataCache = caches.entries
    .firstOrNull { (key, value) -> key.java.isAssignableFrom(clazz.java) }?.value
     ?: throw IllegalStateException("no datacache for for $clazz")

    override suspend fun register(description: DataDescription<out Any, out Any>) {
        caches[description.clazz] = generator(description).also { it.register(description) }
    }

    override suspend fun <T : Any> put(item: T) {
        getCacheOrThrow(item::class).put(item)
    }

    override fun <T : Any> query(clazz: KClass<T>): QueryBuilder<T> {
        return getCacheOrThrow(clazz).query(clazz)
    }
}