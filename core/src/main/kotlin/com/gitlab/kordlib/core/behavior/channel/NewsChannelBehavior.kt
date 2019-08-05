package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateNewsChannelBuilder
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateTextChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The behavior of a Discord News Channel associated to a guild.
 */
@KordPreview
@ExperimentalCoroutinesApi
interface NewsChannelBehavior : GuildMessageChannelBehavior {

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord): NewsChannelBehavior = object : NewsChannelBehavior {
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
 * @return The edited [NewsChannel].
 */
@KordPreview
@ExperimentalCoroutinesApi
suspend inline fun NewsChannelBehavior.edit(block: (UpdateNewsChannelBuilder) -> Unit): Nothing /*NewsChannel*/ = TODO()