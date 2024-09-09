@file:Generate(
    INT_KORD_ENUM, name = "EntitlementType",
    docUrl = "https://discord.com/developers/docs/resources/entitlement#entitlement-object-entitlement-types",
    entries = [
        Entry("Purchase", intValue = 1, kDoc = "Entitlement that was purchased by a user."),
        Entry("PremiumSubscription", intValue = 2, kDoc = "Entitlement for a Discord Nitro subscription."),
        Entry("DeveloperGift", intValue = 3, kDoc = "Entitlement that was gifted to a user by the developer."),
        Entry("TestModePurchase", intValue = 4, kDoc = "Entitlement that was purchased by a dev in application test mode."),
        Entry("FreePurchase", intValue = 5, kDoc = "Entitlement that was purchased when the [Sku][DiscordSku] was free."),
        Entry("UserGift", intValue = 6, kDoc = "Entitlement that was gifted to a user by another user."),
        Entry("PremiumPurchase", intValue = 7, kDoc = "Entitlement that was claimed for free as a Nitro subscriber."),
        Entry("ApplicationSubscription", intValue = 8, kDoc = "Entitlement was purchased as an app subscription.")
    ]
)

@file:Generate(
    INT_KORD_ENUM, name = "EntitlementOwnerType",
    docUrl = "https://discord.com/developers/docs/resources/entitlement#create-test-entitlement-json-params",
    entries = [
        Entry("Guild", intValue = 1, kDoc = "Entitlement is owned by a guild."),
        Entry("User", intValue = 2, kDoc = "Entitlement is owned by a user."),
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An instance of a [Discord Entitlement](https://discord.com/developers/docs/resources/entitlement#entitlement-object)
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
    val endsAt: Optional<Instant?> = Optional.Missing(),
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val consumed: OptionalBoolean = OptionalBoolean.Missing,
)
