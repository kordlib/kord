package dev.kord.core.gateway

import dev.kord.gateway.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration

public data class ShardEvent(val event: Event, val gateway: Gateway, val shard: Int)

public interface MasterGateway {
    public val gateways: Map<Int, Gateway>

    /**
     * Calculates the average [Gateway.ping] of all running [gateways].
     *
     * Gateways that return `null` are not counted into the average, if all [gateways]
     * return `null` then this property will return `null` as well.
     */
    public val averagePing: Duration?


    @OptIn(FlowPreview::class)
    public val events: Flow<ShardEvent>

    public suspend fun startWithConfig(configuration: GatewayConfiguration): Unit = coroutineScope {
        gateways.entries.forEach { (shard, gateway) ->
            val config = configuration.copy(shard = configuration.shard.copy(index = shard))
            launch {
                gateway.start(config)
            }
        }
    }

    public suspend fun sendAll(command: Command): Unit = gateways.values.forEach { it.send(command) }

    public suspend fun detachAll(): Unit = gateways.values.forEach { it.detach() }

    public suspend fun stopAll(): Unit = gateways.values.forEach { it.stop() }
}

public suspend inline fun MasterGateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    val builder = GatewayConfigurationBuilder(token)
    builder.apply(config)
    startWithConfig(builder.build())
}
