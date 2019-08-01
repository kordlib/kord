package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateStoreChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface StoreChannelBehavior : GuildMessageChannelBehavior<UpdateStoreChannelBuilder> {

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object: StoreChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}