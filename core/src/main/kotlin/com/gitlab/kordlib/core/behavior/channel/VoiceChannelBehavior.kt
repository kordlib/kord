package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.VoiceState
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.rest.builder.channel.VoiceChannelModifyBuilder
import com.gitlab.kordlib.rest.service.patchVoiceChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a Discord Voice Channel associated to a guild.
 */
interface VoiceChannelBehavior : GuildChannelBehavior {

    /**
     * Requests to retrieve the voice states of this channel, if cached.
     */
    val voiceStates: Flow<VoiceState>
        get() =
            kord.cache.find<VoiceStateData> { VoiceStateData::channelId eq id.longValue }
                    .asFlow()
                    .map { VoiceState(it, kord) }

    /**
     * Requests to get the this behavior as a [VoiceChannel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel(): VoiceChannel = super.asChannel() as VoiceChannel

    /**
     * returns a new [VoiceChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */

    override fun withStrategy(strategy: EntitySupplyStrategy): VoiceChannelBehavior = VoiceChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) = object : VoiceChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [VoiceChannel].
 */
suspend inline fun VoiceChannelBehavior.edit(builder: VoiceChannelModifyBuilder.() -> Unit): VoiceChannel {
    val response = kord.rest.channel.patchVoiceChannel(id.value, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as VoiceChannel
}