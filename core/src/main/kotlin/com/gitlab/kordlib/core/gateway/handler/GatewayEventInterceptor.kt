package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import io.ktor.util.error
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import com.gitlab.kordlib.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

@Suppress("EXPERIMENTAL_API_USAGE")
class GatewayEventInterceptor(
        kord: Kord,
        private val gateway: MasterGateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) {

    private val listeners = listOf(
            MessageEventHandler(kord, gateway, cache, coreEventChannel),
            ChannelEventHandler(kord, gateway, cache, coreEventChannel),
            GuildEventHandler(kord, gateway, cache, coreEventChannel),
            LifeCycleEventHandler(kord, gateway, cache, coreEventChannel),
            UserEventHandler(kord, gateway, cache, coreEventChannel),
            VoiceEventHandler(kord, gateway, cache, coreEventChannel),
            WebhookEventHandler(kord, gateway, cache, coreEventChannel)
    )

    suspend fun start() = coroutineScope {
        gateway.events.onEach { (event, _, shard) -> dispatch(event, shard) }.launchIn(this)
    }

    private suspend fun dispatch(event: Event, shard: Int) {
        runCatching {
            listeners.forEach { it.handle(event, shard) }
        }.onFailure {
            logger.error(it)
        }
    }

}
