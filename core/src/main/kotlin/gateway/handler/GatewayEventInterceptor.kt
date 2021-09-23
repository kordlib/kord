package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import dev.kord.core.event.Event as CoreEvent

abstract class GatewayEventInterceptor {

    suspend fun start(events: Flow<ShardEvent>, coreEvents: MutableSharedFlow<CoreEvent>, kord: Kord): Job = events
        .buffer(Channel.UNLIMITED)
        .onEach { event ->
            val coreEvent = handle(event, kord)
            coreEvent?.let { coreEvents.emit(it) }
        }
        .launchIn(kord)

    abstract suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent?
}