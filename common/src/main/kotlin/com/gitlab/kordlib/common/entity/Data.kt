package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordPinsUpdateData(
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("last_pin_timestamp")
        /*
        Do not trust the docs:
        2020-11-06 Docs mention this being optional only, but unpinning a channel results
        in this field being null.
        */
        val lastPinTimestamp: Optional<String?> = Optional.Missing()
)

@Serializable
data class DiscordTyping(
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("user_id")
        val userId: Snowflake,
        val timestamp: Long,
        val member: Optional<DiscordGuildMember> = Optional.Missing()
)

