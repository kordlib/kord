package com.gitlab.kordlib.core.gateway

import com.gitlab.kordlib.gateway.Command
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.GatewayConfiguration
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlin.time.Duration
import kotlin.time.milliseconds

class MasterGateway(
        private val gateways: List<Gateway>,
        private val shards: List<Int>
) : Gateway {

    init {
        require(gateways.size == shards.size) { "amount of gateways must equal amount of shards" }
    }

    override val ping: Duration
        get() = gateways.sumByDouble { it.ping.inMilliseconds }.milliseconds

    @FlowPreview
    override val events: Flow<Event>
        get() = gateways.asFlow().flatMapMerge(gateways.size) { it.events }

    override suspend fun start(configuration: GatewayConfiguration) = gateways.forEachIndexed { index, gateway ->
        val config = configuration.copy(shard = configuration.shard.copy(index = shards[index]))
        gateway.start(config)
    }

    override suspend fun send(command: Command) = gateways.forEach { it.send(command) }

    override suspend fun detach() = gateways.forEach { it.detach() }

    override suspend fun stop() = gateways.forEach { it.stop() }

}


