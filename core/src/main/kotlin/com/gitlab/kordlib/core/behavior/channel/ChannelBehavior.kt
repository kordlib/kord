package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Strategilizable
import com.gitlab.kordlib.core.entity.channel.Channel

/**
 * The behavior of a [Discord Channel](https://discordapp.com/developers/docs/resources/channel)
 */
interface ChannelBehavior : Entity, Strategilizable {
    /**
     * The raw mention of this entity.
     */
    val mention get() = "<#${id.value}>"

    /**
     * Requests to get this behavior as a [Channel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    suspend fun asChannel(): Channel = strategy.supply(kord).getChannel(id)!!

    /**
     * Requests to get this behavior as a [Channel].
     *
     * Entities will be fetched from the [RestClient][Kord.rest] directly, ignoring the [cache][Kord.cache].
     * Unless the currency of data is important, it is advised to use [asChannel] instead to reduce unneeded API calls.
     */
    suspend fun requestChannel(): Channel = kord.rest.getChannel(id)!!

    /**
     * Requests to delete a channel (or close it if this is a dm channel).
     */
    suspend fun delete() {
        kord.rest.channel.deleteChannel(id.value)
    }


    /**
     * returns a new [ChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */

    fun withStrategy(strategy: EntitySupplyStrategy) = ChannelBehavior(id, kord, strategy)

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) = object : ChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy

        }
    }
}

