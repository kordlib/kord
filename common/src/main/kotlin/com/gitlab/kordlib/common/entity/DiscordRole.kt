package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class DiscordRole(
        val id: String,
        val name: String,
        val color: Int,
        val hoist: Boolean,
        val position: Int,
        val permissions: Permissions,
        val managed: Boolean,
        val mentionable: Boolean
)

@Serializable
@KordUnstableApi
data class DiscordAuditLogRoleChange(
        val id: String,
        val name: String? = null,
        val color: Int? = null,
        val hoist: Boolean? = null,
        val position: Int? = null,
        val permissions: Permissions? = null,
        val managed: Boolean? = null,
        val mentionable: Boolean? = null
)

@Serializable
@KordUnstableApi
data class DiscordGuildRole(
        @SerialName("guild_id")
        val guildId: String,
        val role: DiscordRole
)

@Serializable
@KordUnstableApi
data class DiscordDeletedGuildRole(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("role_id")
        val id: String
)