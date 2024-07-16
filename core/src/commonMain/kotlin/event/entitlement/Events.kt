package dev.kord.core.event.entitlement

import dev.kord.core.Kord
import dev.kord.core.entity.Entitlement
import dev.kord.core.entity.Sku
import dev.kord.core.entity.User
import dev.kord.core.event.Event

/**
 * An [Event] that is sent when an [Entitlement] is created.
 *
 * An [Entitlement] is created when a [User] subscribes to a [Sku].
 */
public class EntitlementCreateEvent(
    /** The [Entitlement] that was created. */
    public val entitlement: Entitlement,
    override val shard: Int,
    override val customContext: Any?,
    override val kord: Kord
) : Event {

    override fun toString(): String {
        return "EntitlementCreateEvent(entitlement=$entitlement, customContext=$customContext, kord=$kord, shard=$shard)"
    }

}

/**
 * An [Event] that is sent when an [Entitlement] is updated.
 *
 * An [Entitlement] is updated when a [User]'s subscription renews for the next billing period. When an [Entitlement]
 * for a subscription is renewed, [entitlement.endsAt][Entitlement.endsAt] may have an updated value with the new
 * expiration date.
 *
 * If a [User]'s subscription is cancelled or expires, you will *not* receive an [EntitlementDeleteEvent]. Instead, you
 * will simply not receive an [EntitlementUpdateEvent] with a new [entitlement.endsAt][Entitlement.endsAt] date at the
 * end of the billing period.
 */
public class EntitlementUpdateEvent(
    /** The [Entitlement] that was updated. */
    public val entitlement: Entitlement,
    /** The [entitlement] as found in [cache][Kord.cache] before the update. */
    public val old: Entitlement?,
    override val shard: Int,
    override val customContext: Any?,
    override val kord: Kord
) : Event {

    override fun toString(): String {
        return "EntitlementUpdateEvent(entitlement=$entitlement, old=$old, customContext=$customContext, kord=$kord, shard=$shard)"
    }

}

/**
 * An [Event] that is sent when an [Entitlement] is deleted.
 *
 * [Entitlement] deletions are infrequent, and occur when:
 * - Discord issues a refund for a subscription
 * - Discord removes an [Entitlement] from a [User] via internal tooling
 *
 * If a [User]'s subscription is cancelled or expires, you will *not* receive an [EntitlementDeleteEvent]. Instead, you
 * will simply not receive an [EntitlementUpdateEvent] with a new [entitlement.endsAt][Entitlement.endsAt] date at the
 * end of the billing period.
 */
public class EntitlementDeleteEvent(
    /** The [Entitlement] that was deleted */
    public val entitlement: Entitlement,
    override val shard: Int,
    override val customContext: Any?,
    override val kord: Kord
) : Event {
    override fun toString(): String {
        return "EntitlementDeleteEvent(entitlement=$entitlement, customContext=$customContext, kord=$kord, shard=$shard)"
    }
}
