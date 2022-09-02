@file:GenerateKordEnum(
    name = "IntegrationExpireBehavior", valueType = INT,
    entries = [
        Entry("RemoveRole", intValue = 0),
        Entry("Kick", intValue = 1),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.serialization.DurationInDays
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.DeprecationLevel.ERROR

@Serializable
public data class DiscordIntegration(
    val id: Snowflake,
    val name: String,
    val type: String,
    val enabled: Boolean,
    val syncing: Boolean,
    @SerialName("role_id")
    val roleId: Snowflake,
    @SerialName("enable_emoticons")
    val enableEmoticons: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("expire_behavior")
    val expireBehavior: IntegrationExpireBehavior,
    @SerialName("expire_grace_period")
    val expireGracePeriod: DurationInDays,
    val user: DiscordUser,
    val account: DiscordIntegrationsAccount,
    @SerialName("synced_at")
    val syncedAt: Instant,
    val subscriberCount: Int,
    val revoked: Boolean,
    val application: IntegrationApplication
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
    /** @suppress */
    @Deprecated("This is deprecated and will always be empty.", level = ERROR)
    val summary: String,
    val bot: Optional<DiscordUser> = Optional.Missing(),
)

@Serializable
public data class DiscordIntegrationsAccount(
    val id: String,
    val name: String
)
