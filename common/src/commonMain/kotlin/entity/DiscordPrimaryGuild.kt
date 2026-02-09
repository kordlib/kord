package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The representation of a users primary guild
 *
 * @property identityGuildId The ID of the users primary guild
 * @property identityEnabled Whether the user is displaying the primary guild's server tag
 * @property tag The text of the user's server tag
 * @property badge The server tag badge hash
 */
@Serializable
public data class DiscordPrimaryGuild(
    @SerialName("identity_guild_id")
    val identityGuildId: Snowflake?,
    @SerialName("identity_enabled")
    val identityEnabled: Boolean?,
    val tag: String?,
    val badge: String?
)