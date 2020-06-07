package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.NewsChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.rest.builder.channel.NewsChannelModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.patchNewsChannel

/**
 * The behavior of a Discord News Channel associated to a guild.
 */
interface NewsChannelBehavior : GuildMessageChannelBehavior {

    /**
     * Requests to get the this behavior as a [NewsChannel] through the [strategy].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [NewsChannel].
     */
    override suspend fun asChannel(): NewsChannel = super.asChannel() as NewsChannel

    /**
     * Requests to get this behavior as a [NewsChannel] through the [strategy],
     * returns null if the channel isn't present or if the channel isn't a news channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): NewsChannel? = super.asChannelOrNull() as? NewsChannel

    /**
     * Returns a new [NewsChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): NewsChannelBehavior = NewsChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): NewsChannelBehavior = object : NewsChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun NewsChannelBehavior.edit(builder: NewsChannelModifyBuilder.() -> Unit): NewsChannel {
    val response = kord.rest.channel.patchNewsChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}

