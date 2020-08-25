package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class DiscordEmoji(
        val id: String? = null,
        val name: String? = null,
        val roles: List<String>? = null,
        val user: DiscordUser? = null,
        @SerialName("require_colons")
        val requireColons: Boolean? = null,
        val managed: Boolean? = null,
        val animated: Boolean? = null,
        val available: Boolean? = null
)

@Serializable
@KordUnstableApi
data class DiscordUpdatedEmojis(
        @SerialName("guild_id")
        val guildId: String,
        val emojis: List<DiscordEmoji>
)

@Serializable
@KordUnstableApi
data class DiscordPartialEmoji(
        val id: String? = null,
        val name: String? = null,
        val animated: Boolean? = null
)