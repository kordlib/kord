package dev.kord.core.event.monetization

import dev.kord.common.entity.SubscriptionStatus.Active
import dev.kord.common.entity.SubscriptionStatus.Inactive
import dev.kord.core.Kord
import dev.kord.core.entity.monetization.Entitlement
import dev.kord.core.entity.monetization.Subscription
import dev.kord.core.event.Event

/**
 * An [Event] that is sent when a [Subscription] for a Premium App is created.
 *
 * A [Subscription]'s [status][Subscription.status] can be either [Inactive] or [Active] when this event is received.
 * You will receive subsequent [SubscriptionUpdateEvent]s if the [status][Subscription.status] is updated to [Active].
 * As a best practice, you should not grant any perks to users until the [Entitlement]s are created.
 */
public class SubscriptionCreateEvent(
    /** The [Subscription] that was created. */
    public val subscription: Subscription,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String =
        "SubscriptionCreateEvent(subscription=$subscription, kord=$kord, shard=$shard, customContext=$customContext)"
}

/** An [Event] that is sent when a [Subscription] for a Premium App has been updated. */
public class SubscriptionUpdateEvent(
    /** The [Subscription] that was updated. */
    public val subscription: Subscription,
    /** The [Subscription] as found in [cache][Kord.cache] before the update. */
    public val old: Subscription?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String = "SubscriptionUpdateEvent(subscription=$subscription, old=$old, kord=$kord, " +
        "shard=$shard, customContext=$customContext)"
}

/** An [Event] that is sent when a [Subscription] for a Premium App has been deleted. */
public class SubscriptionDeleteEvent(
    /** The [Subscription] that was deleted. */
    public val subscription: Subscription,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String =
        "SubscriptionDeleteEvent(subscription=$subscription, kord=$kord, shard=$shard, customContext=$customContext)"
}
