package com.gitlab.hopebaron.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinsUpdateData(
        @SerialName("guild_id")
        val guildId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: String? = null
)

@Serializable
data class Typing(
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: Snowflake? = null,
        @SerialName("user_id")
        val userId: Snowflake,
        val timestamp: Long
)

