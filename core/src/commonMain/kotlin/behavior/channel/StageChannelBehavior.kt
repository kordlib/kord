package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.StageInstanceData
import dev.kord.core.entity.StageInstance
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.StageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.StageVoiceChannelModifyBuilder
import dev.kord.rest.builder.guild.CurrentVoiceStateModifyBuilder
import dev.kord.rest.builder.guild.VoiceStateModifyBuilder
import dev.kord.rest.builder.stage.StageInstanceCreateBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.modifyCurrentVoiceState
import dev.kord.rest.service.modifyVoiceState
import dev.kord.rest.service.patchStageVoiceChannel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface StageChannelBehavior : BaseVoiceChannelBehavior, TopGuildMessageChannelBehavior {

    /**
     * Returns a new [StageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>
    ): StageChannelBehavior {
        return StageChannelBehavior(id, guildId, kord, strategy.supply(kord))
    }

    /**
     * Requests to get [StageInstance] for the given channel id. Returns null when the instance is not present.
     *
     * @return A nullable [StageInstance] for the given channel
     *
     * @throws [RequestException] if something went wrong during the request
     */
    public suspend fun getStageInstanceOrNull(): StageInstance? = supplier.getStageInstanceOrNull(id)

    /**
     * Requests the [StageInstance]s for a channel.
     *
     * @return a [StageInstance] for the given channel.
     *
     * @throws RequestException if something went wrong during the request
     * @throws EntityNotFoundException if the state instance was null
     */
    public suspend fun getStageInstance(): StageInstance = supplier.getStageInstance(id)

}

/**
 * Requests to create a new stage instance.
 *
 * @param topic The stage instance topic
 * @param builder The builder for the stage instance
 *
 * @return The created [StageInstance].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun StageChannelBehavior.createStageInstance(
    topic: String,
    builder: StageInstanceCreateBuilder.() -> Unit = {},
): StageInstance {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val instance = kord.rest.stageInstance.createStageInstance(id, topic, builder)
    val data = StageInstanceData.from(instance)
    return StageInstance(data, kord, supplier)
}

/**
 * Requests to edit the current user's voice state in this [StageChannel].
 */
public suspend inline fun StageChannelBehavior.editCurrentVoiceState(builder: CurrentVoiceStateModifyBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    kord.rest.guild.modifyCurrentVoiceState(guildId, builder)
}

/**
 * Requests to edit the another user's voice state in this [StageChannel].
 */
public suspend inline fun StageChannelBehavior.editVoiceState(
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
public suspend fun StageChannelBehavior.edit(builder: StageVoiceChannelModifyBuilder.() -> Unit): StageChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchStageVoiceChannel(id, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as StageChannel
}

public fun StageChannelBehavior(
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
