@file:Generate(
    INT_KORD_ENUM, name = "SubscriptionStatus",
    docUrl = "https://discord.com/developers/docs/resources/subscription#subscription-statuses",
    entries = [
        Entry("Active", intValue = 0, kDoc = "The subscription is active and scheduled to renew."),
        Entry("Ending", intValue = 1, kDoc = "The subscription is active but will not renew."),
        Entry("Inactive", intValue = 2, kDoc = "The subscription is inactive and not being charged."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordSubscription(
    val id: Snowflake,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("sku_ids")
    val skuIds: List<Snowflake>,
    @SerialName("entitlement_ids")
    val entitlementIds: List<Snowflake>,
    @SerialName("current_period_start")
    val currentPeriodStart: Instant,
    @SerialName("current_period_end")
    val currentPeriodEnd: Instant,
    val status: SubscriptionStatus,
    @SerialName("canceled_at")
    val canceledAt: Instant?,
    val country: Optional<String> = Optional.Missing(),
)
