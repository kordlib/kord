package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.cache.api.query

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.VoiceState
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.patchVoiceChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord Voice Channel associated to a guild.
 */
interface VoiceChannelBehavior : GuildChannelBehavior {

    /**
     * Requests to retrieve the present voice states of this channel.
     *
     * This property is not resolvable through REST and will always use [KordCache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val voiceStates: Flow<VoiceState>
        get() = kord.cache.query<VoiceStateData> { VoiceStateData::channelId eq id.value }
                .asFlow()
                .map { VoiceState(it, kord) }

    /**
     * Requests to get the this behavior as a [VoiceChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [VoiceChannel].
     */
    override suspend fun asChannel(): VoiceChannel = super.asChannel() as VoiceChannel

    /**
     * Requests to get this behavior as a [VoiceChannel],
     * returns null if the channel isn't present or if the channel isn't a [VoiceChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): VoiceChannel? = super.asChannelOrNull() as? VoiceChannel

    /**
     * Returns a new [VoiceChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceChannelBehavior = VoiceChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy) = object : VoiceChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id, guildId)

            override fun equals(other: Any?): Boolean = when(other) {
                is GuildChannelBehavior -> other.id == id && other.guildId == guildId
                is ChannelBehavior -> other.id == id
                else -> false
            }

            override fun toString(): String {
                return "VoiceChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
            }
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun VoiceChannelBehavior.edit(builder: VoiceChannelModifyBuilder.() -> Unit): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.channel.patchVoiceChannel(id, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as VoiceChannel
}