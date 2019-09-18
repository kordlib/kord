package com.gitlab.kordlib.core.gateway

import com.gitlab.kordlib.gateway.Command
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.GatewayConfiguration
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.seconds

class MasterGateway(
        private val gateways: List<Gateway>,
        private val shards: List<Int>
) : Gateway {

    init {
        require(gateways.size == shards.size) { "amount of gateways must equal amount of shards" }
    }

    @FlowPreview
    override val events: Flow<Event>
        get() = gateways.asFlow().flatMapMerge(gateways.size) { it.events }

    override suspend fun start(configuration: GatewayConfiguration) {
        gateways.mapIndexed { index, gateway ->
            val config = configuration.copy(shard = configuration.shard.copy(index = shards[index]))
            config to gateway
        }.asFlow().delayEachAfterFirst(5.seconds).collect { (configuration, gateway) ->
            gateway.start(configuration)
        }
    }

    override suspend fun send(command: Command) = gateways.forEach { it.send(command) }

    override suspend fun detach() = gateways.forEach { it.detach() }

    override suspend fun stop() = gateways.forEach { it.stop() }

    private fun<T> Flow<T>.delayEachAfterFirst(duration: Duration) : Flow<T> = flow {
            collect {
                emit(it)
                delay(duration.toLongMilliseconds())
            }
    }

}


