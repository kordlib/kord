package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateTextChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface TextChannelBehavior : GuildMessageChannelBehavior<UpdateTextChannelBuilder> {

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord): TextChannelBehavior = object : TextChannelBehavior {
            override val guildId: Snowflake = guildId
            override val categoryId: Snowflake = categoryId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

@ExperimentalCoroutinesApi
suspend inline fun TextChannelBehavior.edit(block: (UpdateTextChannelBuilder) -> Unit): Nothing /*VoiceChannel*/ =
        edit(UpdateTextChannelBuilder().apply(block))