package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordGuildMember(
        val user: Optional<DiscordUser> = Optional.Missing(),
        /*
        Don't trust the docs:
        2020-11-05 nick is only documented as nullable but can be missing through Gateway
        */
        val nick: Optional<String?> = Optional.Missing(),
        val roles: List<Snowflake>,
        @SerialName("joined_at")
        val joinedAt: String,
        @SerialName("premium_since")
        val premiumSince: Optional<String?> = Optional.Missing(),
        val deaf: Boolean,
        val mute: Boolean
)

@Serializable
data class DiscordAddedGuildMember(
        val user: Optional<DiscordUser> = Optional.Missing(),
        /*
        Don't trust the docs:
        2020-11-05 nick is only documented as nullable but can be missing through Gateway
        */
        val nick: Optional<String?> = Optional.Missing(),
        val roles: List<Snowflake>,
        @SerialName("joined_at")
        val joinedAt: String,
        @SerialName("premium_since")
        val premiumSince: Optional<String?> = Optional.Missing(),
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("guild_id")
        val guildId: Snowflake
)

@Serializable
data class DiscordRemovedGuildMember(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val user: DiscordUser
)

@Serializable
data class DiscordUpdatedGuildMember(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val roles: List<Snowflake>,
        val user: DiscordUser,
        val nick: Optional<String?> = Optional.Missing(),
        @SerialName("joined_at")
        val joinedAt: String,
        @SerialName("premium_since")
        val premiumSince: Optional<String?> = Optional.Missing(),
)
