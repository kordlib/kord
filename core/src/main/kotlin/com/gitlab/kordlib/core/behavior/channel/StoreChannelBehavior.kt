package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateNewsChannelBuilder
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateStoreChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface StoreChannelBehavior : GuildMessageChannelBehavior {

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord): StoreChannelBehavior = object : StoreChannelBehavior {
            override val guildId: Snowflake = guildId
            override val categoryId: Snowflake = categoryId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

suspend inline fun StoreChannelBehavior.edit(block: (UpdateStoreChannelBuilder) -> Unit): Nothing /*NewsChannel*/ = TODO()