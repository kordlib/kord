package dev.kord.core.gateway

import dev.kord.gateway.Gateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds

public class DefaultMasterGateway(
    override val gateways: Map<Int, Gateway>,
): MasterGateway {

    /**
     * Calculates the average [Gateway.ping] of all running [gateways].
     *
     * Gateways that return `null` are not counted into the average, if all [gateways]
     * return `null` then this property will return `null` as well.
     */
    override val averagePing: Duration?
        get(): Duration? {
            val pings = gateways.values.mapNotNull { it.ping.value?.inWholeMicroseconds }
            if (pings.isEmpty()) return null

            return pings.average().microseconds
        }


    override val events: Flow<ShardEvent> = gateways.entries
        .map { (shard, gateway) -> gateway.events.map { ShardEvent(it, gateway, shard) } }
        .merge()


    override fun toString(): String {
        return "MasterGateway(gateways=$gateways)"
    }

}
