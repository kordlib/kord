package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.event.guild.WebhookUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.WebhooksUpdate
import kotlin.coroutines.CoroutineContext
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class WebhookEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent? =
        when (event) {
            is WebhooksUpdate -> handle(event, shard, kord, context)
            else -> null
        }

    private fun handle(event: WebhooksUpdate, shard: Int, kord: Kord, context: CoroutineContext): WebhookUpdateEvent =
        with(event.webhooksUpdateData) {
            return WebhookUpdateEvent(guildId, channelId, kord, shard, coroutineContext = context)
        }

}