package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.core.ClientResources
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import io.ktor.util.error
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import com.gitlab.kordlib.core.event.Event as CoreEvent
import kotlinx.coroutines.channels.Channel as CoroutineChannel

private val logger = KotlinLogging.logger { }

@Suppress("EXPERIMENTAL_API_USAGE")
class GatewayEventInterceptor(
        kord: Kord,
        private val gateway: Gateway,
        cache: DataCache,
        coreEventChannel: CoroutineChannel<CoreEvent>
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
        gateway.events.onEach { event -> dispatch(event) }.launchIn(this)
    }

    private suspend fun dispatch(event: Event) {
        runCatching {
            listeners.forEach { it.handle(event) }
        }.onFailure {
            logger.error(it)
        }
    }

}
