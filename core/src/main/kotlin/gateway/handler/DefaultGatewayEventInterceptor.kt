package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import dev.kord.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

@Suppress("EXPERIMENTAL_API_USAGE")
public class DefaultGatewayEventInterceptor(
    cache: DataCache,
    private val eventScope: ((ShardEvent, Kord) -> CoroutineContext)? = null
) : GatewayEventInterceptor {

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
                val coreEvent = listener.handle(
                    event.event,
                    event.shard,
                    kord,
                    CoroutineScope((eventScope?.invoke(event, kord) ?: kord.coroutineContext)
                            + SupervisorJob(kord.coroutineContext.job))
                )
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
