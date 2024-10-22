package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordSubscription
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.SubscriptionStatus
import dev.kord.common.entity.optional.Optional
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class SubscriptionData(
    val id: Snowflake,
    val userId: Snowflake,
    val skuIds: List<Snowflake>,
    val entitlementIds: List<Snowflake>,
    val currentPeriodStart: Instant,
    val currentPeriodEnd: Instant,
    val status: SubscriptionStatus,
    val canceledAt: Instant?,
    val country: Optional<String> = Optional.Missing(),
) {
    public companion object {
        public val description: DataDescription<SubscriptionData, Snowflake> = description(SubscriptionData::id)

        public fun from(subscription: DiscordSubscription): SubscriptionData = with(subscription) {
            SubscriptionData(
                id = id,
                userId = userId,
                skuIds = skuIds,
                entitlementIds = entitlementIds,
                currentPeriodStart = currentPeriodStart,
                currentPeriodEnd = currentPeriodEnd,
                status = status,
                canceledAt = canceledAt,
                country = country,
            )
        }
    }
}
