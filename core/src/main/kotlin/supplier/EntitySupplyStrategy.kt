package dev.kord.core.supplier

import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplyStrategy.Companion.cache

/**
 *  A supplier that accepts a [Kord] instance and returns an [EntitySupplier] of type [T].
 */
public interface EntitySupplyStrategy<T : EntitySupplier> {

    /**
     * Returns an [EntitySupplier] of type [T] that operates on the [kord] instance.
     */
    public fun supply(kord: Kord): T

    public companion object {

        /**
         * A supplier providing a strategy which exclusively uses REST calls to fetch entities.
         * See [RestEntitySupplier] for more details.
         */
        public val rest: EntitySupplyStrategy<RestEntitySupplier> = object : EntitySupplyStrategy<RestEntitySupplier> {

            override fun supply(kord: Kord): RestEntitySupplier = RestEntitySupplier(kord)

            override fun toString(): String = "EntitySupplyStrategy.rest"
        }

        /**
         * A supplier providing a strategy which exclusively uses cache to fetch entities.
         * See [CacheEntitySupplier] for more details.
         */
        public val cache: EntitySupplyStrategy<CacheEntitySupplier> =
            object : EntitySupplyStrategy<CacheEntitySupplier> {

                override fun supply(kord: Kord): CacheEntitySupplier = CacheEntitySupplier(kord)

                override fun toString(): String = "EntitySupplyStrategy.cache"
            }


        /**
         * A supplier providing a strategy which exclusively uses REST calls to fetch entities.
         * fetched entities are stored in [Kord's cache][Kord.cache].
         * See [StoreEntitySupplier] for more details.
         */
        public val cachingRest: EntitySupplyStrategy<EntitySupplier> = object : EntitySupplyStrategy<EntitySupplier> {
            override fun supply(kord: Kord): EntitySupplier {
                return StoreEntitySupplier(rest.supply(kord), kord.cache)
            }

            override fun toString(): String = "EntitySupplyStrategy.cacheAwareRest"

        }


        /**
         * A supplier providing a strategy which will first operate on the [cache] supplier. When an entity
         * is not present from cache it will be fetched from [rest] instead. Operations that return flows
         * will only fall back to rest when the returned flow contained no elements.
         */
        public val cacheWithRestFallback: EntitySupplyStrategy<EntitySupplier> =
            object : EntitySupplyStrategy<EntitySupplier> {

                override fun supply(kord: Kord): EntitySupplier = cache.supply(kord).withFallback(rest.supply(kord))

                override fun toString(): String = "EntitySupplyStrategy.cacheWithRestFallback"
            }

        /**
         * A supplier providing a strategy which will first operate on the [cache] supplier. When an entity
         * is not present from cache it will be fetched from [cachingRest] instead which will update [cache] with fetched elements.
         * Operations that return flows will only fall back to rest when the returned flow contained no elements.
         */
        public val cacheWithCachingRestFallback: EntitySupplyStrategy<EntitySupplier> =
            object : EntitySupplyStrategy<EntitySupplier> {

                override fun supply(kord: Kord): EntitySupplier =
                    cache.supply(kord).withFallback(cachingRest.supply(kord))

                override fun toString(): String = "EntitySupplyStrategy.cacheWithCacheAwareRestFallback"
            }

        /**
         * Create an [EntitySupplyStrategy] from the given [supplier].
         */
        public operator fun <T : EntitySupplier> invoke(
            supplier: (Kord) -> T
        ): EntitySupplyStrategy<T> = object : EntitySupplyStrategy<T> {
            override fun supply(kord: Kord): T = supplier(kord)
        }
    }

}
