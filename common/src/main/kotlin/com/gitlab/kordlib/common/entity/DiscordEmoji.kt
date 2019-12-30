package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordEmoji(
        val id: String? = null,
        val name: String? = null,
        val roles: List<String>? = null,
        val user: DiscordUser? = null,
        @SerialName("require_colons")
        val requireColons: Boolean? = null,
        val managed: Boolean? = null,
        val animated: Boolean? = null
)

@Serializable
data class DiscordUpdatedEmojis(
        @SerialName("guild_id")
        val guildId: String,
        val emojis: List<DiscordEmoji>
)

@Serializable
data class DiscordPartialEmoji(
        val id: String? = null,
        val name: String? = null,
        val animated: Boolean? = null
)