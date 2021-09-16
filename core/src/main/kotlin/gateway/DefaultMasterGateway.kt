package dev.kord.core.gateway

import dev.kord.gateway.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

class DefaultMasterGateway(
    override val gateways: Map<Int, Gateway>,
): MasterGateway {

    /**
     * Calculates the average [Gateway.ping] of all running [gateways].
     *
     * Gateways that return `null` are not counted into the average, if all [gateways]
     * return `null` then this property will return `null` as well.
     */
    override val averagePing
        get(): Duration? {
            val pings = gateways.values.mapNotNull { it.ping.value?.inWholeMicroseconds }
            if (pings.isEmpty()) return null

            return Duration.microseconds(pings.average())
        }


    @OptIn(FlowPreview::class)
    override val events: Flow<ShardEvent> = gateways.entries.asFlow()
        .map { (shard, gateway) -> gateway.events.map { ShardEvent(it, gateway, shard) } }
        .flattenMerge(gateways.size.coerceAtLeast(1))


    override fun toString(): String {
        return "MasterGateway(gateways=$gateways)"
    }

}
