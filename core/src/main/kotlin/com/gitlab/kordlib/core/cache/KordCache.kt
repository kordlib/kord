package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.DataEntryCache
import com.gitlab.kordlib.cache.api.data.DataDescription
import com.gitlab.kordlib.cache.api.delegate.DelegatingDataCache
import com.gitlab.kordlib.cache.api.delegate.EntrySupplier
import com.gitlab.kordlib.cache.map.MapLikeCollection
import com.gitlab.kordlib.cache.map.internal.MapEntryCache
import com.gitlab.kordlib.cache.map.lruLinkedHashMap
import java.util.concurrent.ConcurrentHashMap

typealias Generator<I, T> = (cache: DataCache, description: DataDescription<T, I>) -> DataEntryCache<out T>

@Suppress("FunctionName")
inline fun KordCache(builder: KordCacheBuilder.() -> Unit): DataCache = KordCacheBuilder().apply(builder).build()

class KordCacheBuilder {
    var defaultGenerator: Generator<Any, Any> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.fromThreadSafe(ConcurrentHashMap()))
    }

    private val descriptionGenerators: MutableMap<DataDescription<*, *>, Generator<*,*>> = mutableMapOf()

    fun<T: Any, I: Any> lruCache(size: Int = 100): Generator<T, I> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(size))
    }

    @Suppress("UNCHECKED_CAST")
    fun<T: Any, I: Any> forDescription(description: DataDescription<T,I>, generator: Generator<T, I>?) {
        if (generator == null) {
            descriptionGenerators.remove(description)
            return
        }
        descriptionGenerators[description] = generator as Generator<*, *>
    }

    fun build(): DataCache = DelegatingDataCache(EntrySupplier.invoke { cache, description ->
        val generator = descriptionGenerators[description] ?: defaultGenerator
        generator(cache, description)
    })

}