package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

interface ChannelBehavior : Entity {
    val mention get() = "<#${id.value}>"

    suspend fun delete() {
        kord.rest.channel.deleteChannel(id.toString())
    }

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : ChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}