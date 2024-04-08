package dev.kord.core.event.entitlement

import dev.kord.core.Kord
import dev.kord.core.entity.Entitlement
import dev.kord.core.event.Event

public class EntitlementCreateEvent(
    public val entitlement: Entitlement,
    override val shard: Int,
    override val customContext: Any?
) : Event {

    override val kord: Kord get() = entitlement.kord

    override fun toString(): String {
        return "EntitlementCreateEvent(entitlement=$entitlement, shard=$shard)"
    }

}

public class EntitlementUpdateEvent(
    public val entitlement: Entitlement,
    override val shard: Int,
    override val customContext: Any?
) : Event {

    override val kord: Kord get() = entitlement.kord

    override fun toString(): String {
        return "EntitlementUpdateEvent(entitlement=$entitlement, shard=$shard)"
    }

}

public class EntitlementDeleteEvent(
    public val entitlement: Entitlement,
    override val shard: Int,
    override val customContext: Any?
) : Event {
    override val kord: Kord get() = entitlement.kord

    override fun toString(): String {
        return "EntitlementDeleteEvent(entitlement=$entitlement, shard=$shard)"
    }
}
