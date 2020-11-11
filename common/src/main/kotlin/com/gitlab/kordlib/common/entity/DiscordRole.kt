package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordRole(
        val id: Snowflake,
        val name: String,
        val color: Int,
        val hoist: Boolean,
        val position: Int,
        val permissions: Permissions,
        val managed: Boolean,
        val mentionable: Boolean
)

@Serializable
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
data class DiscordGuildRole(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val role: DiscordRole
)

@Serializable
data class DiscordDeletedGuildRole(
        @SerialName("guild_id")
        val guildId: Snowflake,
        @SerialName("role_id")
        val id: Snowflake
)