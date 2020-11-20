package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordGuildWidget(
        val enabled: Boolean,
        @SerialName("channel_id")
        val channelId: Snowflake?
)