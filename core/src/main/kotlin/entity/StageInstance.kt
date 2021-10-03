package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.StageInstanceBehavior
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class StageInstance(
    public val data: StageInstanceData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : StageInstanceBehavior {
    override val id: Snowflake get() = data.id
    public val guildId: Snowflake get() = data.guildId
    override val channelId: Snowflake get() = data.channelId
    public val topic: String get() = data.topic

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StageInstanceBehavior =
        StageInstance(data, kord, strategy.supply(kord))

    override suspend fun asStageInstance(): StageInstance = this

    override suspend fun asStageInstanceOrNull(): StageInstance = this
}
