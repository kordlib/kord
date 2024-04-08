@file:Generate(
    Generate.EntityType.INT_KORD_ENUM, name = "EntitlementType",
    docUrl = "https://discord.com/developers/docs/monetization/entitlements#entitlement-object-entitlement-types",
    entries = [
        Generate.Entry("ApplicationSubscription", intValue = 8, kDoc = "Entitlement was purchased as an app subscription.")
    ]
)

@file:Generate(
    Generate.EntityType.INT_KORD_ENUM, name = "EntitlementOwnerType",
    docUrl = "https://discord.com/developers/docs/monetization/entitlements#create-test-entitlement",
    unknownConstructorWasPublic = false,
    entries = [
        Generate.Entry("Guild", intValue = 1, kDoc = "Entitlement is owned by a guild."),
        Generate.Entry("User", intValue = 2, kDoc = "Entitlement is owned by a user."),
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.Generate
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://discord.com/developers/docs/monetization/entitlements#entitlement-object
 */
@Serializable
public data class DiscordEntitlement(
    val id: Snowflake,
    @SerialName("sku_id")
    val skuId: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    val type: EntitlementType,
    val deleted: Boolean,
    @SerialName("starts_at")
    val startsAt: Optional<Instant> = Optional.Missing(),
    @SerialName("ends_at")
    val endsAt: Optional<Instant> = Optional.Missing(),
    @SerialName("guild_Id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
)