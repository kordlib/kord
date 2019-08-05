package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateNewsChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface NewsChannelBehavior : GuildMessageChannelBehavior<UpdateNewsChannelBuilder> {

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord): NewsChannelBehavior = object : NewsChannelBehavior {
            override val guildId: Snowflake = guildId
            override val categoryId: Snowflake = categoryId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}