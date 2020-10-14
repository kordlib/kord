package com.gitlab.kordlib.core.gateway

import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlin.time.milliseconds

data class ShardEvent(val event: Event, val gateway: Gateway, val shard: Int)

class MasterGateway(
        val gateways: Map<Int, Gateway>
) {

    val averagePing get() = gateways.values.asSequence().map { it.ping.inMilliseconds }.average().milliseconds

    val events: Flow<ShardEvent> = gateways.entries.asFlow()
            .flatMapMerge(gateways.size) { (shard, gateway) -> gateway.events.map { event -> ShardEvent(event, gateway, shard) } }

    suspend fun start(configuration: GatewayConfiguration) = gateways.entries.forEach { (shard, gateway) ->
        val config = configuration.copy(shard = configuration.shard.copy(index = shard))
        gateway.start(config)
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
