package dev.kord.core.event

import dev.kord.core.Kord
import dev.kord.gateway.Gateway
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface Event : CoroutineScope {
    /**
     * The Gateway that spawned this event.
     */
    val gateway: Gateway get() = kord.gateway.gateways.getValue(shard)

    val kord: Kord

    /**
     * The shard number of the [gateway] that spawned this event.
     */
    val shard: Int

}