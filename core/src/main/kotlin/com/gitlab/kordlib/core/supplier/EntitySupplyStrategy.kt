package dev.kord.core.supplier

import dev.kord.core.Kord

/**
 *  A supplier that accepts a [Kord] instance and returns an [EntitySupplier] of type [T].
 */
interface EntitySupplyStrategy<T : EntitySupplier> {

    /**
     * Returns an [EntitySupplier] of type [T] that operates on the [kord] instance.
     */
    fun supply(kord: Kord): T

    companion object {

        /**
         * A supplier providing a strategy which exclusively uses REST calls to fetch entities.
         * See [RestEntitySupplier] for more details.
         */
        val rest = object : EntitySupplyStrategy<RestEntitySupplier> {

            override fun supply(kord: Kord): RestEntitySupplier = RestEntitySupplier(kord)

            override fun toString(): String = "EntitySupplyStrategy.rest"
        }

        /**
         * A supplier providing a strategy which exclusively uses cache to fetch entities.
         * See [CacheEntitySupplier] for more details.
         */
        val cache = object : EntitySupplyStrategy<CacheEntitySupplier> {

            override fun supply(kord: Kord): CacheEntitySupplier = CacheEntitySupplier(kord)

            override fun toString(): String = "EntitySupplyStrategy.cache"
        }

        /**
         * A supplier providing a strategy which will first operate on the [cache] supplier. When an entity
         * is not present from cache it will be fetched from [rest] instead. Operations that return flows
         * will only fall back to rest when the returned flow contained no elements.
         */
        val cacheWithRestFallback = object : EntitySupplyStrategy<EntitySupplier> {

            override fun supply(kord: Kord): EntitySupplier = cache.supply(kord).withFallback(rest.supply(kord))

            override fun toString(): String = "EntitySupplyStrategy.cacheWithRestFallback"
        }

        /**
         * Create an [EntitySupplyStrategy] from the given [supplier].
         */
        operator fun <T : EntitySupplier> invoke(
                supplier: (Kord) -> T
        ): EntitySupplyStrategy<T> = object : EntitySupplyStrategy<T> {
            override fun supply(kord: Kord): T = supplier(kord)
        }
    }

}
