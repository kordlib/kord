package dev.kord.core.gateway

import dev.kord.gateway.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.microseconds

data class ShardEvent(val event: Event, val gateway: Gateway, val shard: Int)

class MasterGateway(
    val gateways: Map<Int, Gateway>,
) {

    /**
     * Calculates the average [Gateway.ping] of all running [gateways].
     *
     * Gateways that return `null` are not counted into the average, if all [gateways]
     * return `null` then this property will return `null` as well.
     */
    val averagePing
        get(): Duration? {
            val pings = gateways.values.mapNotNull { it.ping.value?.inMicroseconds }
            if (pings.isEmpty()) return null

            return pings.average().microseconds
        }


    @OptIn(FlowPreview::class)
    val events: Flow<ShardEvent> = gateways.entries.asFlow()
        .map { (shard, gateway) -> gateway.events.map { ShardEvent(it, gateway, shard) } }
        .flattenMerge(gateways.size.coerceAtLeast(1))

    /**
     * Calls [Gateway.start] on each Gateway in [gateways], changing the [GatewayConfiguration.shard] for each Gateway.
     */
    suspend fun start(configuration: GatewayConfiguration): Unit = coroutineScope {
        gateways.entries.forEach { (shard, gateway) ->
            val config = configuration.copy(shard = configuration.shard.copy(index = shard))
            launch {
                gateway.start(config)
            }
        }
    }

    suspend inline fun start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
        val builder = GatewayConfigurationBuilder(token)
        builder.apply(config)
        start(builder.build())
    }

    suspend fun sendAll(command: Command) = gateways.values.forEach { it.send(command) }

    suspend fun detachAll() = gateways.values.forEach { it.detach() }

    suspend fun stopAll() = gateways.values.forEach { it.stop() }

    override fun toString(): String {
        return "MasterGateway(gateways=$gateways)"
    }

}
