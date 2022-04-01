package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.StageInstance
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.StageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.stage.StageInstanceModifyBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface StageInstanceBehavior : KordEntity, Strategizable {

    /** The id of the associated [StageChannel]. */
    public val channelId: Snowflake

    public suspend fun delete(reason: String? = null): Unit = kord.rest.stageInstance.deleteStageInstance(channelId, reason)

    @Suppress("DEPRECATION")
    @Deprecated("Replaced by 'edit'.", ReplaceWith("this.edit {\nthis@edit.topic = topic\n}"))
    public suspend fun update(topic: String): StageInstance {
        val instance = kord.rest.stageInstance.updateStageInstance(channelId, dev.kord.rest.json.request.StageInstanceUpdateRequest(topic))
        val data = StageInstanceData.from(instance)

        return StageInstance(data, kord, supplier)
    }

    /**
     * Requests to get this behavior as a [StageInstance] if it's not an instance of a [StageInstance].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun asStageInstance(): StageInstance = supplier.getStageInstance(channelId)

    /**
     * Requests to get this behavior as a [StageInstance] if it's not an instance of a [StageInstance],
     * returns null if the stage instance isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(channelId)

    /**
     * Retrieve the [StageInstance] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchStageInstance(): StageInstance = supplier.getStageInstance(id)


    /**
     * Retrieve the [StageInstance] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [StageInstance] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StageInstanceBehavior =
        StageInstanceBehavior(id, channelId, kord, strategy.supply(kord))
}

public suspend inline fun StageInstanceBehavior.edit(builder: StageInstanceModifyBuilder.() -> Unit): StageInstance {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val instance = kord.rest.stageInstance.modifyStageInstance(channelId, builder)
    val data = StageInstanceData.from(instance)
    return StageInstance(data, kord, supplier)
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
