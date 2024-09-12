package dev.kord.core.entity.monetization

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.SubscriptionStatus
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.SubscriptionData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.User
import dev.kord.core.hash
import kotlinx.datetime.Instant

/**
 * An instance of a [Subscription](https://discord.com/developers/docs/resources/subscription).
 *
 * Subscriptions represent a [User] making recurring payments for at least one [Sku] over an ongoing period. Successful
 * payments grant the [User] access to entitlements associated with the [Sku].
 */
public class Subscription(
    public val data: SubscriptionData,
    override val kord: Kord,
) : KordEntity {
    override val id: Snowflake get() = data.id

    /** The ID of the [User] who is subscribed. */
    public val userId: Snowflake get() = data.userId

    /** The behavior of the [User] who is subscribed. */
    public val user: UserBehavior get() = UserBehavior(userId, kord)

    /** The list of [Sku]s subscribed to. */
    public val skuIds: List<Snowflake> get() = data.skuIds

    /** The list of [Entitlement]s granted for this subscription. */
    public val entitlementIds: List<Snowflake> get() = data.entitlementIds

    /** The start date of the current subscription period. */
    public val currentPeriodStart: Instant get() = data.currentPeriodStart

    /** The end date of the current subscription period. */
    public val currentPeriodEnd: Instant get() = data.currentPeriodEnd

    /** The current status of this subscription. */
    public val status: SubscriptionStatus get() = data.status

    /** When this subscription was canceled. */
    public val canceledAt: Instant? get() = data.canceledAt

    /**
     * The ISO3166-1 alpha-2 country code of the payment source used to purchase this subscription.
     *
     * Missing unless queried with a private OAuth scope.
     */
    public val country: String? get() = data.country.value

    /** The start date of this subscription. */
    public val startsAt: Instant get() = id.timestamp

    override fun equals(other: Any?): Boolean =
        other is Subscription && this.id == other.id && this.userId == other.userId

    override fun hashCode(): Int = hash(id, userId)

    override fun toString(): String = "Subscription(data=$data, kord=$kord)"
}
