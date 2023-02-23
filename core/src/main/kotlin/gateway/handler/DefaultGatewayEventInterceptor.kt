package dev.kord.core.gateway.handler

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import io.ktor.util.logging.*
import mu.KotlinLogging
import dev.kord.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

public typealias CustomContextCreator = suspend (event: ShardEvent, kord: Kord) -> Any?

/** Used to make sure [customContextCreator] is only invoked when needed. */
internal class LazyContext(
    private val event: ShardEvent,
    private val kord: Kord,
    private val customContextCreator: CustomContextCreator,
) {
    suspend fun get() = customContextCreator(event, kord)
}

/**
 * Default implementation of [GatewayEventInterceptor] that also updates [cache][Kord.cache].
 *
 * @param customContextCreator This function is invoked inside of [handle] to create an object that is inserted into
 * [customContext][CoreEvent.customContext]. Note that it will only be invoked if this particular [handle] invocation
 * actually creates an [Event][CoreEvent].
 */
public class DefaultGatewayEventInterceptor @KordPreview public constructor(
    private val customContextCreator: CustomContextCreator?,
) : GatewayEventInterceptor {

    // overload instead of default parameters to allow changes to @KordPreview constructor
    // without breaking binary compatibility for non preview no-arg constructor
    public constructor() : this(customContextCreator = null)

    private val listeners = listOf(
        UnknownEventHandler(),
        AutoModerationEventHandler(),
        ChannelEventHandler(),
        GuildEventHandler(),
        InteractionEventHandler(),
        LifeCycleEventHandler(),
        MessageEventHandler(),
        ThreadEventHandler(),
        UserEventHandler(),
        VoiceEventHandler(),
        WebhookEventHandler(),
    )

    override suspend fun handle(event: ShardEvent, kord: Kord): CoreEvent? {
        return runCatching {
            val context = customContextCreator?.let { LazyContext(event, kord, it) }
            for (listener in listeners) {
                val coreEvent = listener.handle(event.event, event.shard, kord, context)
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
