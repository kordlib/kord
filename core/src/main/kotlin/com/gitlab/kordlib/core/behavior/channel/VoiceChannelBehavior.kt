package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateVoiceChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake

interface VoiceChannelBehavior : GuildChannelBehavior<UpdateVoiceChannelBuilder> {

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : VoiceChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

suspend inline fun VoiceChannelBehavior.edit(block: (UpdateVoiceChannelBuilder) -> Unit): Nothing /*VoiceChannel*/ =
        edit(UpdateVoiceChannelBuilder().apply(block))