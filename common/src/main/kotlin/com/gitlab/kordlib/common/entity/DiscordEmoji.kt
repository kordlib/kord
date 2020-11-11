package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordEmoji(
        val id: Snowflake?,
        val name: String?,
        val roles: Optional<List<Snowflake>> = Optional.Missing(),
        val user: Optional<DiscordUser> = Optional.Missing(),
        @SerialName("require_colons")
        val requireColons: OptionalBoolean = OptionalBoolean.Missing,
        val managed: OptionalBoolean = OptionalBoolean.Missing,
        val animated: OptionalBoolean = OptionalBoolean.Missing,
        val available: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
data class DiscordUpdatedEmojis(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val emojis: List<DiscordEmoji>,
)

@Serializable
data class DiscordPartialEmoji(
        val id: Snowflake?,
        val name: String?,
        val animated: OptionalBoolean = OptionalBoolean.Missing,
)