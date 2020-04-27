package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.rest.builder.channel.TextChannelModifyBuilder
import com.gitlab.kordlib.rest.service.patchTextChannel


interface TextChannelBehavior : GuildMessageChannelBehavior {

    /**
     * Requests to get the this behavior as a [TextChannel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel(): TextChannel =  super.asChannel() as TextChannel

    /**
     * Requests to get this behavior as a [TextChannel].
     *
     * Entities will be fetched from the [RestClient][Kord.rest] directly, ignoring the [cache][Kord.cache].
     * Unless the currency of data is important, it is advised to use [asChannel] instead to reduce unneeded API calls.
     */
    override suspend fun requestChannel(): TextChannel = super.requestChannel() as TextChannel

    /**
     * returns a new [TextChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): TextChannelBehavior = TextChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): TextChannelBehavior = object : TextChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy        }
    }

}

/**
 * Requests to edit this channel.
 *
 * @return The edited [TextChannel].
 */
suspend inline fun TextChannelBehavior.edit(builder: TextChannelModifyBuilder.() -> Unit): TextChannel {
    val response = kord.rest.channel.patchTextChannel(id.value, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as TextChannel
}
