package dev.kord.core.cache.api

import kotlin.reflect.typeOf

public class BasicRelation<T: Any> : Relation<T> {
    private val relationships: MutableSet<RelationHandler<T, Any>> = mutableSetOf()
    private val caches: TypedCache = TypedSetCache()
    override fun discard(value: T) {
        relationships.onEach { relate ->
            getCaches().toSet().onEach { otherCache ->
                otherCache.discardIf { relate(value, it) }
            }
        }
    }
    override fun getCaches(): TypedCache {
        return caches
    }

    override fun <T : Any> putCache(cache: EntryCache<T>) {
        caches.putCache(cache)
    }

    override fun <R : Any> relate(handler: RelationHandler<T, R>) {
        relationships.add(safe(handler))
    }
    private fun <R: Any> safe(handler: RelationHandler<T, R>): RelationHandler<T, Any> {
        return object : RelationHandler<T, Any> {
            override fun invoke(value: T, friend: Any): Boolean {
                @Suppress("UNCHECKED_CAST")
                val safeFriend = friend as? R ?: return false
                return handler(value, safeFriend)
            }
        }
    }
}