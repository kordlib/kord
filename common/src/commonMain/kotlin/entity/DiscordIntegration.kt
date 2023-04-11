@file:GenerateKordEnum(
    name = "IntegrationExpireBehavior", valueType = INT,
    deprecatedSerializerName = "Serializer",
    docUrl = "https://discord.com/developers/docs/resources/guild#integration-object-integration-expire-behaviors",
    entries = [
        Entry("RemoveRole", intValue = 0),
        Entry("Kick", intValue = 1),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInDays
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordIntegration(
    val id: Snowflake,
    val name: String,
    val type: String,
    val enabled: Boolean,
    val syncing: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("role_id")
    val roleId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("enable_emoticons")
    val enableEmoticons: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("expire_behavior")
    val expireBehavior: Optional<IntegrationExpireBehavior> = Optional.Missing(),
    @SerialName("expire_grace_period")
    val expireGracePeriod: Optional<DurationInDays> = Optional.Missing(),
    val user: Optional<DiscordUser> = Optional.Missing(),
    val account: DiscordIntegrationsAccount,
    @SerialName("synced_at")
    val syncedAt: Optional<Instant> = Optional.Missing(),
    @SerialName("subscriber_count")
    val subscriberCount: OptionalInt = OptionalInt.Missing,
    val revoked: OptionalBoolean = OptionalBoolean.Missing,
    val application: Optional<IntegrationApplication> = Optional.Missing(),
    @SerialName("guild_id") // available in Integration Create and Integration Update events
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
public data class DiscordPartialIntegration(
    val id: Snowflake,
    val name: String,
    val type: String,
    val account: DiscordIntegrationsAccount,
)

@Serializable
public data class IntegrationApplication(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val description: String,
    val bot: Optional<DiscordUser> = Optional.Missing(),
)

@Serializable
public data class DiscordIntegrationsAccount(
    val id: String,
    val name: String
)
