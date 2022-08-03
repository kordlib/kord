package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent
import dev.kord.gateway.Event as GatewayEvent

internal abstract class BaseGatewayEventHandler(
    protected val cache: DataCache
) {

    abstract suspend fun handle(event: GatewayEvent, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent?
}
