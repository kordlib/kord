package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.rest.builder.channel.TextChannelModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.patchTextChannel

interface TextChannelBehavior : GuildMessageChannelBehavior {

    /**
     * Requests to get the this behavior as a [TextChannel] through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [TextChannel].
     */
    override suspend fun asChannel(): TextChannel = super.asChannel() as TextChannel

    /**
     * Requests to get this behavior as a [TextChannel] through the [strategy],
     * returns null if the channel isn't present or if the channel isn't a [TextChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): TextChannel? = super.asChannelOrNull() as? TextChannel


    /**
     * Returns a new [TextChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): TextChannelBehavior = TextChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(
                guildId: Snowflake,
                id: Snowflake,
                kord: Kord,
                strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
        ): TextChannelBehavior = object : TextChannelBehavior {
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
 * @return The edited [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun TextChannelBehavior.edit(builder: TextChannelModifyBuilder.() -> Unit): TextChannel {
    val response = kord.rest.channel.patchTextChannel(id.value, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as TextChannel
}
