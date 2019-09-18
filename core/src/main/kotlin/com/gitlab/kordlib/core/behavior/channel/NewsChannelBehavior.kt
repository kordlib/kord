package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.builder.channel.UpdateNewsChannelBuilder
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.NewsChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The behavior of a Discord News Channel associated to a guild.
 */
@KordPreview
@ExperimentalCoroutinesApi
interface NewsChannelBehavior : GuildMessageChannelBehavior {

    override suspend fun asChannel(): NewsChannel {
        return super.asChannel() as NewsChannel
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): NewsChannelBehavior = object : NewsChannelBehavior {
            override val guildId: Snowflake = guildId
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
@Suppress("NAME_SHADOWING")
suspend inline fun NewsChannelBehavior.edit(builder: (UpdateNewsChannelBuilder) -> Unit): NewsChannel {
    val builder = UpdateNewsChannelBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.channel.patchChannel(id.value, request, reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}