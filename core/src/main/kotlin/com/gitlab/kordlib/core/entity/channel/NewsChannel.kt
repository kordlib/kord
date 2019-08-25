package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.NewsChannelCreateBuilder
import com.gitlab.kordlib.core.`object`.data.ChannelData
import com.gitlab.kordlib.core.behavior.channel.NewsChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.StoreChannelBehavior

/**
 * An instance of a Discord News Channel associated to a guild.
 */
@KordPreview
data class NewsChannel(override val data: ChannelData, override val kord: Kord) : CategorizableChannel, GuildMessageChannel, NewsChannelBehavior {
    override suspend fun asChannel(): NewsChannel = this
}