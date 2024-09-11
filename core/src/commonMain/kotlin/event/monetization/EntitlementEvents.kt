package dev.kord.core.event.monetization

import dev.kord.core.Kord
import dev.kord.core.entity.User
import dev.kord.core.entity.monetization.Entitlement
import dev.kord.core.event.Event

/**
 * An [Event] that is sent when an [Entitlement] is created.
 */
public class EntitlementCreateEvent(
    /** The [Entitlement] that was created. */
    public val entitlement: Entitlement,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String =
        "EntitlementCreateEvent(entitlement=$entitlement, kord=$kord, shard=$shard, customContext=$customContext)"
}

/**
 * An [Event] that is sent when an [Entitlement] is updated.
 *
 * An [Entitlement] is updated when a subscription is canceled, [entitlement.endsAt][Entitlement.endsAt] indicates the
 * end date.
 */
public class EntitlementUpdateEvent(
    /** The [Entitlement] that was updated. */
    public val entitlement: Entitlement,
    /** The [entitlement] as found in [cache][Kord.cache] before the update. */
    public val old: Entitlement?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String = "EntitlementUpdateEvent(entitlement=$entitlement, old=$old, kord=$kord, " +
        "shard=$shard, customContext=$customContext)"
}

/**
 * An [Event] that is sent when an [Entitlement] is deleted.
 *
 * [Entitlement] deletions are infrequent, and occur when:
 * - Discord issues a refund for a subscription
 * - Discord removes an [Entitlement] from a [User] via internal tooling
 * - Discord deletes an app-managed [Entitlement] they created via the API
 *
 * [Entitlement]s are _not_ deleted when they expire.
 */
public class EntitlementDeleteEvent(
    /** The [Entitlement] that was deleted. */
    public val entitlement: Entitlement,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String =
        "EntitlementDeleteEvent(entitlement=$entitlement, kord=$kord, shard=$shard, customContext=$customContext)"
}
