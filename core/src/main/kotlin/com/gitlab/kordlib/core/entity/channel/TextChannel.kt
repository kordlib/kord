package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData

/**
 * An instance of a Discord Text Channel associated to a guild.
 */
data class TextChannel(override val data: ChannelData, override val kord: Kord) : GuildMessageChannel, TextChannelBehavior {

    /**
     * Whether the channel is nsfw.
     */
    val isNsfw: Boolean get() = data.nsfw!!

    /**
     * The amount of seconds a user has to wait before sending another message.
     */
    val userRateLimit: Int get() = data.rateLimitPerUser!!

    override suspend fun asChannel(): TextChannel = this

}
