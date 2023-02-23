package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.event.UnknownEvent
import dev.kord.gateway.Event
import dev.kord.gateway.UnknownDispatchEvent

internal class UnknownEventHandler : BaseGatewayEventHandler() {
    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        context: LazyContext?
    ): dev.kord.core.event.Event? {
        return if (event is UnknownDispatchEvent) {
            UnknownEvent(
                kord, shard, context?.get(), event.data, event.name
            )
        } else {
            null
        }
    }
}
