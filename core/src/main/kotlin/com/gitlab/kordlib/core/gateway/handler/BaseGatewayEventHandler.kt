package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.Gateway
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import com.gitlab.kordlib.core.event.Event as CoreEvent
import com.gitlab.kordlib.gateway.Event as GatewayEvent

abstract class BaseGatewayEventHandler(
        protected val kord: Kord,
        protected val gateway: MasterGateway,
        protected val cache: DataCache,
        protected val coreFlow: MutableSharedFlow<CoreEvent>
) {

    abstract suspend fun handle(event: GatewayEvent, shard: Int)

}