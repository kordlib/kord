package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.Gateway
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.kord.core.event.Event as CoreEvent
import dev.kord.gateway.Event as GatewayEvent

abstract class BaseGatewayEventHandler(
    protected val kord: Kord,
    protected val gateway: MasterGateway,
    protected val cache: DataCache,
    protected val coreFlow: MutableSharedFlow<CoreEvent>
) {

    abstract suspend fun handle(event: GatewayEvent, shard: Int)

}