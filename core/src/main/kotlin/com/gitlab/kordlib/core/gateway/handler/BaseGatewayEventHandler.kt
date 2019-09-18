package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.gateway.Gateway
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent
import com.gitlab.kordlib.gateway.Event as GatewayEvent

abstract class BaseGatewayEventHandler(
        protected val kord: Kord,
        protected val gateway: Gateway,
        protected val cache: DataCache,
        protected val coreEventChannel: SendChannel<CoreEvent>
) {

    abstract suspend fun handle(event: GatewayEvent)

}