package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.ChannelType.*
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData

/**
 * An instance of a [Discord Channel](https://discordapp.com/developers/docs/resources/channel)
 */
interface Channel : ChannelBehavior {
    val data: ChannelData

    override val id: Snowflake
        get() = Snowflake(data.id)

    /**
     * The type of this channel.
     */
    val type: ChannelType get() = data.type

    companion object {
        @Suppress("EXPERIMENTAL_API_USAGE")
        fun from(data: ChannelData, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): Channel = when (data.type) {
            GuildText -> TextChannel(data, kord)
            DM, GroupDm -> DmChannel(data, kord)
            GuildVoice -> VoiceChannel(data, kord)
            GuildCategory -> Category(data, kord)
            GuildNews -> NewsChannel(data, kord)
            GuildStore -> StoreChannel(data, kord)
            else -> object: Channel {
                override val data: ChannelData = data
                override val kord: Kord = kord
                override val strategy: EntitySupplyStrategy = strategy
            }
        }
    }
}