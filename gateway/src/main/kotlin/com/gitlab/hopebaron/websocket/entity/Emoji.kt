package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
        val id: String?,
        val name: String,
        val roles: List<Role>?,
        val user: User?,
        @SerialName("require_colons")
        val requireColons: Boolean?,
        val managed: Boolean?,
        val animated: Boolean?
)

@Serializable
data class UpdatedEmojis(
        @SerialName("guild_id")
        val guildId: String,
        val emojis: List<Emoji>
)
