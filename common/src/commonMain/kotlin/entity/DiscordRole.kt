@file:Generate(
    INT_FLAGS, name = "RoleFlag",
    docUrl = "https://discord.com/developers/docs/topics/permissions#role-object-role-flags",
    entries = [
        Entry("InPrompt", shift = 0, kDoc = "Role can be selected by members in an onboarding prompt."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordRole(
    val id: Snowflake,
    val name: String,
    val color: Int,
    val hoist: Boolean,
    val icon: Optional<String?> = Optional.Missing,
    @SerialName("unicode_emoji")
    val unicodeEmoji: Optional<String?> = Optional.Missing,
    val position: Int,
    val permissions: Permissions,
    val managed: Boolean,
    val mentionable: Boolean,
    val tags: Optional<DiscordRoleTags> = Optional.Missing,
    val flags: RoleFlags,
)

@Serializable
public data class DiscordRoleTags(
    @SerialName("bot_id")
    val botId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("integration_id")
    val integrationId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("premium_subscriber")
    val premiumSubscriber: Optional<Nothing?> = Optional.Missing,
    @SerialName("subscription_listing_id")
    val subscriptionListingId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("available_for_purchase")
    val availableForPurchase: Optional<Nothing?> = Optional.Missing,
    @SerialName("guild_connections")
    val guildConnections: Optional<Nothing?> = Optional.Missing,
)

@Serializable
public data class DiscordPartialRole(
    val id: Snowflake,
    val name: Optional<String> = Optional.Missing,
    val color: OptionalInt = OptionalInt.Missing,
    val hoist: OptionalBoolean = OptionalBoolean.Missing,
    val icon: Optional<String?> = Optional.Missing,
    @SerialName("unicode_emoji")
    val unicodeEmoji: Optional<String?> = Optional.Missing,
    val position: OptionalInt = OptionalInt.Missing,
    val permissions: Optional<Permissions> = Optional.Missing,
    val managed: OptionalBoolean = OptionalBoolean.Missing,
    val mentionable: OptionalBoolean = OptionalBoolean.Missing,
    val tags: Optional<DiscordRoleTags> = Optional.Missing,
)

@Serializable
public data class DiscordAuditLogRoleChange(
    val id: String,
    val name: String? = null,
    val color: Int? = null,
    val hoist: Boolean? = null,
    val position: Int? = null,
    val permissions: Permissions? = null,
    val managed: Boolean? = null,
    val mentionable: Boolean? = null,
)

@Serializable
public data class DiscordGuildRole(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val role: DiscordRole,
)

@Serializable
public data class DiscordDeletedGuildRole(
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("role_id")
    val id: Snowflake,
)
