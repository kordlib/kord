package dev.kord.core.event

import dev.kord.core.Kord
import dev.kord.gateway.Gateway
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public interface Event : CoroutineScope {
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
