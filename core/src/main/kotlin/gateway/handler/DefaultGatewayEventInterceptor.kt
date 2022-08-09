package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import io.ktor.util.logging.*
import mu.KotlinLogging
import dev.kord.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

/** Default implementation of [GatewayEventInterceptor] that also updates [cache][Kord.cache]. */
public class DefaultGatewayEventInterceptor : GatewayEventInterceptor {

    private val listeners = listOf(
        MessageEventHandler(),
        ChannelEventHandler(),
        ThreadEventHandler(),
        GuildEventHandler(),
        LifeCycleEventHandler(),
        UserEventHandler(),
        VoiceEventHandler(),
        WebhookEventHandler(),
        InteractionEventHandler()
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
