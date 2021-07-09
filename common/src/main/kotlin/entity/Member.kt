package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
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
    val deaf: OptionalBoolean = OptionalBoolean.Missing,
    val mute: OptionalBoolean = OptionalBoolean.Missing,
    val pending: OptionalBoolean = OptionalBoolean.Missing
)


@Serializable
data class DiscordInteractionGuildMember(
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
    val permissions: Permissions,
    val pending: OptionalBoolean = OptionalBoolean.Missing
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
    val guildId: Snowflake,
    val pending: OptionalBoolean = OptionalBoolean.Missing
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
    val pending: OptionalBoolean = OptionalBoolean.Missing
)

@Serializable
data class DiscordThreadMember(
    val id: Snowflake,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("join_timestamp")
    val joinTimestamp: String,
    val flags: Int
)