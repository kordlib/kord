package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
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
data class AuditLogRoleChange(
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
data class GuildRole(
        @SerialName("guild_id")
        val guildId: String,
        val role: Role
)

@Serializable
data class DeletedGuildRole(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("role_id")
        val roleId: String
)