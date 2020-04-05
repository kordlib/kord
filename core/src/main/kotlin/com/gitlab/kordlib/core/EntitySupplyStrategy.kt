package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import kotlinx.coroutines.flow.Flow


interface EntitySupplyStrategy {

    fun supply(kord: Kord) : EntitySupplier

    companion object {
        operator fun invoke(supplier: (Kord) -> EntitySupplier) = object : EntitySupplyStrategy {
            override fun supply(kord: Kord): EntitySupplier = supplier(kord)
        }
    }

    object Rest : EntitySupplyStrategy {
        override fun supply(kord: Kord): EntitySupplier = kord.rest
    }

    object Cache : EntitySupplyStrategy {
        override fun supply(kord: Kord): EntitySupplier = kord.cache
    }

    object CacheWithRestFallback : EntitySupplyStrategy {
        override fun supply(kord: Kord): EntitySupplier = kord.cache.withFallback(kord.rest)
    }

}