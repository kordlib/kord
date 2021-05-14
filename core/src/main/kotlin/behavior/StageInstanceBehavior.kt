package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.StageInstance
import dev.kord.core.entity.Strategizable
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.json.request.StageInstanceUpdateRequest

interface StageInstanceBehavior : KordEntity, Strategizable {
    val channelId: Snowflake

    suspend fun delete(): Unit = kord.rest.stageInstance.deleteStageInstance(channelId)

    suspend fun update(topic: String): StageInstance {
        val instance = kord.rest.stageInstance.updateStageInstance(channelId, StageInstanceUpdateRequest(topic))
        val data = StageInstanceData.from(instance)

        return StageInstance(data, kord, supplier)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StageInstanceBehavior =
        StageInstanceBehavior(id, channelId, kord, strategy.supply(kord))
}

internal fun StageInstanceBehavior(id: Snowflake, channelId: Snowflake, kord: Kord, supplier: EntitySupplier) =
    object : StageInstanceBehavior {
        override val channelId: Snowflake
            get() = channelId
        override val kord: Kord
            get() = kord
        override val id: Snowflake
            get() = id
        override val supplier: EntitySupplier
            get() = supplier
    }
