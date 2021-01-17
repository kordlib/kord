package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.event.guild.WebhookUpdateEvent
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.Event
import dev.kord.gateway.WebhooksUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class WebhookEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is WebhooksUpdate -> handle(event, shard)
        else -> Unit
    }

    private suspend fun handle(event: WebhooksUpdate, shard: Int) = with(event.webhooksUpdateData) {
        coreFlow.emit(WebhookUpdateEvent(guildId, channelId, kord, shard))
    }

}