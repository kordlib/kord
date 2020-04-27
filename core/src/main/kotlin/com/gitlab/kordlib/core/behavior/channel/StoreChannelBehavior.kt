package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.StoreChannel
import com.gitlab.kordlib.rest.builder.channel.StoreChannelModifyBuilder
import com.gitlab.kordlib.rest.service.patchStoreChannel

/**
 * The behavior of a Discord Store Channel associated to a guild.
 */
interface StoreChannelBehavior : GuildChannelBehavior {

    /**
     * Requests to get the this behavior as a [StoreChannel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel(): StoreChannel =  super.asChannel() as StoreChannel

    /**
     * Requests to get this behavior as a [StoreChannel].
     *
     * Entities will be fetched from the [RestClient][Kord.rest] directly, ignoring the [cache][Kord.cache].
     * Unless the currency of data is important, it is advised to use [asChannel] instead to reduce unneeded API calls.
     */
    override suspend fun requestChannel(): StoreChannel = super.requestChannel() as StoreChannel

    /**
     * returns a new [StoreChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): StoreChannelBehavior = StoreChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): StoreChannelBehavior = object : StoreChannelBehavior {
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
 * @return The edited [StoreChannel].
 */
suspend inline fun StoreChannelBehavior.edit(builder: StoreChannelModifyBuilder.() -> Unit): StoreChannel {
    val response = kord.rest.channel.patchStoreChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as StoreChannel
}

