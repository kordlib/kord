package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.StageInstance
import dev.kord.core.entity.Strategizable
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.json.request.StageInstanceUpdateRequest

interface StageInstanceBehavior : KordEntity, Strategizable {
    val channelId: Snowflake

    suspend fun delete(reason: String? = null): Unit = kord.rest.stageInstance.deleteStageInstance(channelId, reason)

    suspend fun update(topic: String): StageInstance {
        val instance = kord.rest.stageInstance.updateStageInstance(channelId, StageInstanceUpdateRequest(topic))
        val data = StageInstanceData.from(instance)

        return StageInstance(data, kord, supplier)
    }

    /**
     * Requests to get the this behavior as a [StageInstance] if it's not an instance of a [StageInstance].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    suspend fun asStageInstance(): StageInstance = supplier.getStageInstance(channelId)

    /**
     * Requests to get this behavior as a [StageInstance] if its not an instance of a [StageInstance],
     * returns null if the user isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(channelId)

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

        override fun toString(): String {
            return "StageInstanceBehavior(id=$id, channelId=$id, kord=$kord, supplier=$supplier)"
        }
    }
