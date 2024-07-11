@file:Generate(
    INT_KORD_ENUM, name = "SkuType",
    docUrl = "https://discord.com/developers/docs/monetization/skus#sku-object-sku-types",
    entries = [
        Entry("Durable", intValue = 2, kDoc = "A durable, one-time purchase."),
        Entry("Consumable", intValue = 3, kDoc = "A consumable, one-time purchase."),
        Entry("Subscription", intValue = 5, kDoc = "Represents a recurring subscription."),
        Entry("SubscriptionGroup", intValue = 6, kDoc = "System-generated group for each [Subscription] SKU created."),
    ]
)

@file:Generate(
    INT_FLAGS, name = "SkuFlag",
    docUrl = "https://discord.com/developers/docs/monetization/skus#sku-object-sku-flags",
    entries = [
        Entry("Available", shift = 2, kDoc = "SKU is available for purchase."),
        Entry(
            "GuildSubscription", shift = 7,
            kDoc = "Recurring SKU that can be purchased by a user and applied to a single server. Grants access to every user in that server."
        ),
        Entry(
            "UserSubscription", shift = 8,
            kDoc = "Recurring SKU purchased by a user for themselves. Grants access to the purchasing user in every server."
        ),
    ]
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_FLAGS
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a [Discord SKU](https://discord.com/developers/docs/monetization/skus#sku-object).
 */
@Serializable
public data class DiscordSku(
    val id: Snowflake,
    val type: SkuType,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val name: String,
    val slug: String,
    val flags: SkuFlags,
)
