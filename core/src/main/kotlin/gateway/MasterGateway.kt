package dev.kord.core.gateway

import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.DeprecationLevel.WARNING
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
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


    public val events: Flow<ShardEvent>

    @Deprecated(
        "Specify maxConcurrency. It can be obtained by calling the Route.GatewayBotGet endpoint.",
        ReplaceWith("this.startWithConfig(configuration, maxConcurrency = Int.MAX_VALUE)"),
        level = WARNING,
    )
    public suspend fun startWithConfig(configuration: GatewayConfiguration): Unit =
        startWithConfig(configuration, maxConcurrency = Int.MAX_VALUE)

    public suspend fun startWithConfig(configuration: GatewayConfiguration, maxConcurrency: Int) {
        require(maxConcurrency > 0) { "Invalid maxConcurrency: $maxConcurrency" }

        // see https://discord.com/developers/docs/topics/gateway#sharding-max-concurrency
        return coroutineScope {

            var previousRateLimitKey = -1
            val readyListeners = mutableListOf<Job>()

            // sort gateways.entries to start shards in order
            for ((shardId, gateway) in gateways.entries.sortedBy { it.key }) {
                require(shardId >= 0) { "Negative shardId: $shardId" }

                val rateLimitKey = shardId % maxConcurrency

                if (rateLimitKey <= previousRateLimitKey) {
                    readyListeners.joinAll() // wait until all gateways from last bucket are started
                    readyListeners.clear()
                }

                // make sure we don't miss the event by executing until first suspension point before starting gateway
                readyListeners += launch(start = UNDISPATCHED) { gateway.events.first { it is Ready } }

                val config = configuration.copy(shard = configuration.shard.copy(index = shardId))
                launch { gateway.start(config) }

                previousRateLimitKey = rateLimitKey
            }

            readyListeners.clear()
        }
    }

    public suspend fun sendAll(command: Command): Unit = gateways.values.forEach { it.send(command) }

    public suspend fun detachAll(): Unit = gateways.values.forEach { it.detach() }

    public suspend fun stopAll(): Unit = gateways.values.forEach { it.stop() }
}

@Deprecated(
    "Specify maxConcurrency. It can be obtained by calling the Route.GatewayBotGet endpoint.",
    ReplaceWith("this.start(token, maxConcurrency = Int.MAX_VALUE) { config() }"),
    level = WARNING,
)
public suspend inline fun MasterGateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    contract { callsInPlace(config, EXACTLY_ONCE) }

    start(token, maxConcurrency = Int.MAX_VALUE, config)
}

public suspend inline fun MasterGateway.start(
    token: String,
    maxConcurrency: Int,
    config: GatewayConfigurationBuilder.() -> Unit = {},
) {
    contract { callsInPlace(config, EXACTLY_ONCE) }

    val configuration = GatewayConfigurationBuilder(token).apply(config).build()
    startWithConfig(configuration, maxConcurrency)
}
