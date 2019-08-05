package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a [Discord Channel](https://discordapp.com/developers/docs/resources/channel)
 */
interface ChannelBehavior : Entity {
    /**
     * The raw mention of this entity.
     */
    val mention get() = "<#${id.value}>"

    /**
     * Sends a request to delete a channel (or close it if this is a private channel).
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