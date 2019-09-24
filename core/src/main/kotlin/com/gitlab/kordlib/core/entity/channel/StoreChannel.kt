package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.StoreChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData

/**
 * An instance of a Discord Store Channel associated to a guild.
 */
data class StoreChannel(override val data: ChannelData, override val kord: Kord) : CategorizableChannel, GuildChannel, StoreChannelBehavior {
    override suspend fun asChannel(): StoreChannel = this
}