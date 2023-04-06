package dev.kord.core.gateway

import dev.kord.gateway.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration

/**
 * The event dispatched for a shard event.
 *
 * @property event The event that was sent
 * @property gateway The [Gateway] the event was sent through
 * @property shard The shard number that the event was sent through
 */
public data class ShardEvent(val event: Event, val gateway: Gateway, val shard: Int)

public interface MasterGateway {
    /**
     * A [Map] of all [Gateway]s for the bot, identified by their [Int] value in map
     */
    public val gateways: Map<Int, Gateway>

    /**
     * Calculates the average [Gateway.ping] of all running [gateways].
     *
     * Gateways that return `null` are not counted into the average, if all [gateways]
     * return `null` then this property will return `null` as well.
     */
    public val averagePing: Duration?


    /**
     * The [Flow] of [ShardEvent]s through the gateway.
     */
    public val events: Flow<ShardEvent>

    /**
     * Starts the gateway with the desired [configuration][GatewayConfiguration].
     *
     * @param configuration The desired configuration to start the gateway with.
     */
    public suspend fun startWithConfig(configuration: GatewayConfiguration): Unit = coroutineScope {
        gateways.entries.forEach { (shard, gateway) ->
            val config = configuration.copy(shard = configuration.shard.copy(index = shard))
            launch {
                gateway.start(config)
            }
        }
    }

    /**
     * Sends a [command] to all [gateways], suspending until the message has been sent.
     *
     * @param command The [Command] to send to the gateway
     * @throws Exception When the gateway isn't open
     */
    public suspend fun sendAll(command: Command): Unit = gateways.values.forEach { it.send(command) }

    /**
     * Closes all gateways and releases all resources.
     * **For some implementations this will render the Gateway unopenable,
     * as such, all implementations should be handled as if they are irreversibly closed.**
     */
    public suspend fun detachAll(): Unit = gateways.values.forEach { it.detach() }

    /**
     * Closes all Gateways and ends the current sessions, suspending until the underlying webSocket is closed.
     */
    public suspend fun stopAll(): Unit = gateways.values.forEach { it.stop() }
}

/**
 * Starts the gateway.
 *
 * @param token The Bot token
 * @param config The configuration to start the gateway with
 */
public suspend inline fun MasterGateway.start(token: String, config: GatewayConfigurationBuilder.() -> Unit = {}) {
    contract { callsInPlace(config, EXACTLY_ONCE) }

    val configuration = GatewayConfigurationBuilder(token).apply(config).build()
    startWithConfig(configuration)
}
