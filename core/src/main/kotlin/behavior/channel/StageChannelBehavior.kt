package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.entity.StageInstance
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.StageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.StageVoiceChannelModifyBuilder
import dev.kord.rest.builder.guild.CurrentVoiceStateModifyBuilder
import dev.kord.rest.builder.guild.VoiceStateModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.createStageInstance
import dev.kord.rest.service.modifyCurrentVoiceState
import dev.kord.rest.service.modifyVoiceState
import dev.kord.rest.service.patchStageVoiceChannel
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface StageChannelBehavior : BaseVoiceChannelBehavior {

    /**
     * Returns a new [StageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>
    ): StageChannelBehavior {
        return StageChannelBehavior(id, guildId, kord, strategy.supply(kord))
    }


    suspend fun tageInstance(topic: String): StageInstance {
        val instance = kord.rest.stageInstance.createStageInstance(id, topic)
        val data = StageInstanceData.from(instance)

        return StageInstance(data, kord, supplier)
    }

    suspend fun getStageInstanceOrNull(): StageInstance? =
        withStrategy(EntitySupplyStrategy.rest).supplier.getStageInstanceOrNull(id)

    suspend fun getStageInstance(): StageInstance =
        withStrategy(EntitySupplyStrategy.rest).supplier.getStageInstance(id)

}

/**
 * Requests to edit the current user's voice state in this [StageChannel].
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun StageChannelBehavior.editCurrentVoiceState(builder: CurrentVoiceStateModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.guild.modifyCurrentVoiceState(guildId, id, builder)
}

/**
 * Requests to edit the another user's voice state in this [StageChannel].
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun StageChannelBehavior.editVoiceState(
    userId: Snowflake,
    builder: VoiceStateModifyBuilder.() -> Unit
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.guild.modifyVoiceState(guildId, id, userId, builder)
}

/**
 * Requests to edit this channel.
 *
 * @return The edited [StageChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend fun StageChannelBehavior.edit(builder: StageVoiceChannelModifyBuilder.() -> Unit): StageChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchStageVoiceChannel(id, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as StageChannel
}

fun StageChannelBehavior(
    id: Snowflake,
    guildId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): StageChannelBehavior = object : StageChannelBehavior {
    override val guildId: Snowflake
        get() = guildId
    override val kord get() = kord
    override val id: Snowflake get() = id
    override val supplier get() = supplier

    override fun toString(): String {
        return "StageChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}
