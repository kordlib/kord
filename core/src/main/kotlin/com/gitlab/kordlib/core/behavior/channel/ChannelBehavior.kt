package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.toData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.channel.Channel

/**
 * The behavior of a [Discord Channel](https://discordapp.com/developers/docs/resources/channel)
 */
interface ChannelBehavior : Entity {
    /**
     * The raw mention of this entity.
     */
    val mention get() = "<#${id.value}>"

    /**
     * Requests to get this behavior as a [Channel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    suspend fun asChannel(): Channel = kord.getChannel(id)!!

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

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : ChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}