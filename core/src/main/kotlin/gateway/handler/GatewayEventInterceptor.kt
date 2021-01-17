package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.Event
import io.ktor.util.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import dev.kord.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

@Suppress("EXPERIMENTAL_API_USAGE")
class GatewayEventInterceptor(
        private val kord: Kord,
        private val gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>,
) {

    private val listeners = listOf(
            MessageEventHandler(kord, gateway, cache, coreFlow),
            ChannelEventHandler(kord, gateway, cache, coreFlow),
            GuildEventHandler(kord, gateway, cache, coreFlow),
            LifeCycleEventHandler(kord, gateway, cache, coreFlow),
            UserEventHandler(kord, gateway, cache, coreFlow),
            VoiceEventHandler(kord, gateway, cache, coreFlow),
            WebhookEventHandler(kord, gateway, cache, coreFlow),
            InteractionEventHandler(kord,gateway, cache, coreFlow)
    )

    suspend fun start() = gateway.events
                .buffer(Channel.UNLIMITED)
                .onEach { (event, _, shard) -> dispatch(event, shard) }
                .launchIn(kord)

    private suspend fun dispatch(event: Event, shard: Int) {
        runCatching {
            listeners.forEach { it.handle(event, shard) }
        }.onFailure {
            logger.error(it)
        }
    }

}
