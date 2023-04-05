package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.core.Kord
import dev.kord.core.behavior.StageInstanceBehavior
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.entity.channel.StageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class StageInstance(
    public val data: StageInstanceData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : StageInstanceBehavior {
    override val id: Snowflake get() = data.id

    /** The guild id of the associated [StageChannel]. */
    public val guildId: Snowflake get() = data.guildId
    override val channelId: Snowflake get() = data.channelId

    /** The topic of this Stage Instance. */
    public val topic: String get() = data.topic

    /** The [privacy level][StageInstancePrivacyLevel] of this Stage Instance.  */
    public val privacyLevel: StageInstancePrivacyLevel get() = data.privacyLevel

    /** The id of the [GuildScheduledEvent] for this Stage Instance, if it belongs to an event. */
    public val guildScheduledEventId: Snowflake? get() = data.guildScheduledEventId

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StageInstance =
        StageInstance(data, kord, strategy.supply(kord))

    override suspend fun asStageInstance(): StageInstance = this

    override suspend fun asStageInstanceOrNull(): StageInstance = this
}
