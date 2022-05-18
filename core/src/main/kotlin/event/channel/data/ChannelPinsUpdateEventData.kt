package dev.kord.core.event.channel.data

import dev.kord.common.entity.DiscordPinsUpdateData
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class ChannelPinsUpdateEventData(
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val channelId: Snowflake,
    val lastPinTimestamp: Optional<Instant?> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordPinsUpdateData): ChannelPinsUpdateEventData = with(entity) {
            ChannelPinsUpdateEventData(guildId, channelId, lastPinTimestamp)
        }
    }
}
