package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.event.guild.WebhookUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.WebhooksUpdate
import dev.kord.core.event.Event as CoreEvent

internal class WebhookEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord): CoreEvent? =
        when (event) {
            is WebhooksUpdate -> handle(event, shard, kord)
            else -> null
        }

    private fun handle(event: WebhooksUpdate, shard: Int, kord: Kord): WebhookUpdateEvent =
        with(event.webhooksUpdateData) {
            WebhookUpdateEvent(guildId, channelId, kord, shard)
        }
}
