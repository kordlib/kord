package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.VoiceChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import java.util.*

/**
 * An instance of a Discord Voice Channel associated to a guild.
 */
data class VoiceChannel(override val data: ChannelData, override val kord: Kord) : CategorizableChannel, VoiceChannelBehavior {

    /**
     * The bitrate (in bits) of this channel.
     */
    val bitrate: Int get() = data.bitrate!!

    /**
     * The user limit of the voice channel.
     */
    val userLimit: Int get() = data.userLimit!!

    override suspend fun asChannel(): VoiceChannel = this

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when(other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }
}