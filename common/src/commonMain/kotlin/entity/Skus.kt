@file:Generate(
    Generate.EntityType.INT_KORD_ENUM, name =  "SkuType",
    docUrl = "https://discord.com/developers/docs/monetization/skus#sku-object-sku-types",
    entries = [
        Generate.Entry("Subscription", intValue = 5,
            kDoc = "Represents a recurring subscription"),
        Generate.Entry("SubscriptionGroup", intValue = 6,
            kDoc = "System-generated group for each [Subscription] SKU created")
    ]
)

@file:Generate(
    Generate.EntityType.INT_FLAGS, name = "SkuFlag",
    docUrl = "https://discord.com/developers/docs/monetization/skus#sku-object-sku-flags",
    entries = [
        Generate.Entry("Available", shift = 2,
            kDoc = "SKU is available for purchase"),
        Generate.Entry("GuildSubscription", shift = 7,
            kDoc = "Recurring SKU that can be purchased by a user and applied to a single server. Grants access to every user in that server."),
        Generate.Entry("UserSubscription", shift = 8,
            kDoc = "Recurring SKU purchased by a user for themselves. Grants access to the purchasing user in every server."),
    ]
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordSKU(
    val id: Snowflake,
    val type: SkuType,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val name: String,
    val slug: String,
    val flags: SkuFlags,
)
