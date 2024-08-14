@file:Generate(
    INT_FLAGS, name = "GuildMemberFlag", valueName = "code",
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-member-object-guild-member-flags",
    entries = [
        Entry("DidRejoin", shift = 0, kDoc = "Member has left and rejoined the guild."),
        Entry("CompletedOnboarding", shift = 1, kDoc = "Member has completed onboarding."),
        Entry("BypassesVerification", shift = 2, kDoc = "Member is exempt from guild verification requirements."),
        Entry("StartedOnboarding", shift = 3, kDoc = "Member has started onboarding."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildMember(
    val user: Optional<DiscordUser> = Optional.Missing(),
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    @SerialName("joined_at")
    val joinedAt: Instant?,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val deaf: OptionalBoolean = OptionalBoolean.Missing,
    val mute: OptionalBoolean = OptionalBoolean.Missing,
    val flags: GuildMemberFlags,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)


@Serializable
public data class DiscordInteractionGuildMember(
    val user: Optional<DiscordUser> = Optional.Missing(),
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    @SerialName("joined_at")
    val joinedAt: Instant?,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val permissions: Permissions,
    val flags: GuildMemberFlags,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)


@Serializable
public data class DiscordAddedGuildMember(
    val user: Optional<DiscordUser> = Optional.Missing(),
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    @SerialName("joined_at")
    val joinedAt: Instant?,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val deaf: Boolean,
    val mute: Boolean,
    val flags: GuildMemberFlags,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)

@Serializable
public data class DiscordRemovedGuildMember(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val user: DiscordUser
)

@Serializable
public data class DiscordUpdatedGuildMember(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val roles: List<Snowflake>,
    val user: DiscordUser,
    val nick: Optional<String?> = Optional.Missing(),
    @SerialName("joined_at")
    val joinedAt: Instant?,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val flags: GuildMemberFlags,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)

@Serializable
public data class DiscordThreadMember(
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("join_timestamp")
    val joinTimestamp: Instant,
    val flags: Int
)
