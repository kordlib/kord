package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import dev.kord.core.event.Event as CoreEvent

interface GatewayEventInterceptor {

    suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent?
}