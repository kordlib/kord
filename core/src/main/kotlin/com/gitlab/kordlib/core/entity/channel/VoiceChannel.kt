package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.VoiceChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData

/**
 * An instance of a Discord Voice Channel associated to a guild.
 */
data class VoiceChannel(override val data: ChannelData, override val kord: Kord, override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : CategorizableChannel, VoiceChannelBehavior {


    /**
     * The bitrate (in bits) of this channel.
     */
    val bitrate: Int get() = data.bitrate!!

    /**
     * The user limit of the voice channel.
     */
    val userLimit: Int get() = data.userLimit!!

    override suspend fun asChannel(): VoiceChannel = this
}