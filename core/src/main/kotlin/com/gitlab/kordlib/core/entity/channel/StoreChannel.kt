package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.data.ChannelData
import com.gitlab.kordlib.core.behavior.channel.StoreChannelBehavior

/**
 * An instance of a Discord Store Channel associated to a guild.
 */
@KordPreview
data class StoreChannel(override val data: ChannelData, override val kord: Kord) : CategorizableChannel, GuildMessageChannel, StoreChannelBehavior {
    override suspend fun asChannel(): StoreChannel = this
}