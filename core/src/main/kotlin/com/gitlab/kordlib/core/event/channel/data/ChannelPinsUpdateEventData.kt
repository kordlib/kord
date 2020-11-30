package dev.kord.core.event.channel.data

import dev.kord.common.entity.DiscordPinsUpdateData
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.gateway.ChannelPinsUpdate
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