package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.NewsChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData

/**
 * An instance of a Discord News Channel associated to a guild.
 */
data class NewsChannel(override val data: ChannelData, override val kord: Kord, override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) : CategorizableChannel, GuildMessageChannel, NewsChannelBehavior {

    override suspend fun asChannel(): NewsChannel = this
}