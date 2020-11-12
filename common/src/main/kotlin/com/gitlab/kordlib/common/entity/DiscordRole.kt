package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalInt
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
data class DiscordPartialRole(
        val id: Snowflake,
        val name: Optional<String> = Optional.Missing(),
        val color: OptionalInt = OptionalInt.Missing,
        val hoist: OptionalBoolean = OptionalBoolean.Missing,
        val position: OptionalInt = OptionalInt.Missing,
        val permissions: Optional<Permissions> = Optional.Missing(),
        val managed: OptionalBoolean = OptionalBoolean.Missing,
        val mentionable: OptionalBoolean = OptionalBoolean.Missing
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