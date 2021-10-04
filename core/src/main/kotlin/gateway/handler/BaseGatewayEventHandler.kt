package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import kotlin.coroutines.CoroutineContext
import dev.kord.core.event.Event as CoreEvent
import dev.kord.gateway.Event as GatewayEvent

public abstract class BaseGatewayEventHandler(
    protected val cache: DataCache
) {

    public abstract suspend fun handle(event: GatewayEvent, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent?

}
