package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.gateway.ShardEvent
import io.ktor.util.logging.*
import mu.KotlinLogging
import dev.kord.core.event.Event as CoreEvent

private val logger = KotlinLogging.logger { }

public typealias CustomContextCreator = suspend (event: ShardEvent, kord: Kord) -> Any?

/**
 * Default implementation of [GatewayEventInterceptor] that also updates [cache][Kord.cache].
 *
 * @param customContextCreator This function is invoked once per [handle] call to create an object that is inserted
 * into [customContext][CoreEvent.customContext]. Note that this object might not be used if this particular [handle]
 * invocation does not create an [Event][CoreEvent].
 */
public class DefaultGatewayEventInterceptor(
    private val customContextCreator: CustomContextCreator? = null,
) : GatewayEventInterceptor {

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
            val context = customContextCreator?.invoke(event, kord)
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
