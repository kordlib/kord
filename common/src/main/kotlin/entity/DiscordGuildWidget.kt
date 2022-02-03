package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildWidget(
    val enabled: Boolean,
    @SerialName("channel_id")
    val channelId: Snowflake?
)
