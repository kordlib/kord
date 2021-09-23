package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import io.ktor.util.*
import mu.KotlinLogging
import dev.kord.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultGatewayEventInterceptor(
    cache: DataCache
) : GatewayEventInterceptor() {

    private val listeners = listOf(
        MessageEventHandler(cache),
        ChannelEventHandler(cache),
        ThreadEventHandler(cache),
        GuildEventHandler(cache),
        LifeCycleEventHandler(cache),
        UserEventHandler(cache),
        VoiceEventHandler(cache),
        WebhookEventHandler(cache),
        InteractionEventHandler(cache)
    )

    override suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent? {
        return runCatching {
            for (listener in listeners) {
                val coreEvent = listener.handle(event.event, event.shard, kord)
                if (coreEvent != null) {
                    return coreEvent
                }
            }
            return null
        }.onFailure {
            logger.error(it)
        }.getOrNull()
    }

}
