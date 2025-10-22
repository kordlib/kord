package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordPrimaryGuild(
    @SerialName("identity_guild_id")
    val identityGuildId: Snowflake?,
    @SerialName("identity_enabled")
    val identityEnabled: Boolean?,
    val tag: String?,
    val badge: String?
)