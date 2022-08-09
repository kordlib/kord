package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.event.Event as CoreEvent
import dev.kord.gateway.Event as GatewayEvent

public abstract class BaseGatewayEventHandler {

    public abstract suspend fun handle(event: GatewayEvent, shard: Int, kord: Kord, context: Any?): CoreEvent?

}
