package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.event.guild.WebhookUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.WebhooksUpdate
import dev.kord.core.event.Event as CoreEvent

internal class WebhookEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is WebhooksUpdate -> handle(event, shard, kord, context)
            else -> null
        }

    private suspend fun handle(event: WebhooksUpdate, shard: Int, kord: Kord, context: LazyContext?): WebhookUpdateEvent =
        with(event.webhooksUpdateData) {
            WebhookUpdateEvent(guildId, channelId, kord, shard, context?.get())
        }

}
