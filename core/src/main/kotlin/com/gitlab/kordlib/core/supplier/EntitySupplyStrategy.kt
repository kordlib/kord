package com.gitlab.kordlib.core.supplier

import com.gitlab.kordlib.core.Kord

interface EntitySupplyStrategy<T : EntitySupplier> {

    fun supply(kord: Kord): T


    companion object {
        val rest = object : EntitySupplyStrategy<RestEntitySupplier> {

            override fun supply(kord: Kord): RestEntitySupplier = RestEntitySupplier(kord)

        }

        val cache = object : EntitySupplyStrategy<CacheEntitySupplier> {

            override fun supply(kord: Kord): CacheEntitySupplier = CacheEntitySupplier(kord)

        }

        val cacheWithRestFallback = object : EntitySupplyStrategy<EntitySupplier> {

            override fun supply(kord: Kord): EntitySupplier = cache.supply(kord).withFallback(rest.supply(kord))

        }

        operator fun <T : EntitySupplier> invoke(
                supplier: (Kord) -> T
        ): EntitySupplyStrategy<T> = object : EntitySupplyStrategy<T> {
            override fun supply(kord: Kord): T = supplier(kord)
        }
    }

}
