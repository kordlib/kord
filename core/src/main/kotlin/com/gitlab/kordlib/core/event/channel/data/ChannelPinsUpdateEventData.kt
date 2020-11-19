package com.gitlab.kordlib.core.event.channel.data

import com.gitlab.kordlib.common.entity.DiscordPinsUpdateData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.gateway.ChannelPinsUpdate
import kotlinx.serialization.Serializable

@Serializable
data class ChannelPinsUpdateEventData(
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val channelId: Snowflake,
        val lastPinTimestamp: Optional<String?> = Optional.Missing()
) {
    companion object {
        fun from(entity: DiscordPinsUpdateData): ChannelPinsUpdateEventData = with(entity){
            ChannelPinsUpdateEventData(guildId, channelId, lastPinTimestamp)
        }
    }
}