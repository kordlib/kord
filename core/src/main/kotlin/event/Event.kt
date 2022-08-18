package dev.kord.core.event

import dev.kord.core.Kord
import dev.kord.gateway.Gateway

public interface Event {
    /**
     * The Gateway that spawned this event.
     */
    public val gateway: Gateway get() = kord.gateway.gateways.getValue(shard)

    public val kord: Kord

    /**
     * The shard number of the [gateway] that spawned this event.
     */
    public val shard: Int

}
