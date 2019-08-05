package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateVoiceChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a Discord Voice Channel associated to a guild.
 */
interface VoiceChannelBehavior : CategorizableChannelBehavior {

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord) = object : VoiceChannelBehavior {
            override val guildId: Snowflake = guildId
            override val categoryId: Snowflake = categoryId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [VoiceChannel].
 */
suspend inline fun VoiceChannelBehavior.edit(block: (UpdateVoiceChannelBuilder) -> Unit): Nothing /*VoiceChannel*/ = TODO()