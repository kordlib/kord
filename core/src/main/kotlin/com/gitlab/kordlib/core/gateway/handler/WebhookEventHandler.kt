package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.guild.WebhookUpdateEvent
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.WebhooksUpdate
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class WebhookEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is WebhooksUpdate -> handle(event, shard)
        else -> Unit
    }

    private suspend fun handle(event: WebhooksUpdate, shard: Int) = with(event.webhooksUpdateData) {
        coreEventChannel.send(WebhookUpdateEvent(guildId, channelId, kord, shard))
    }

}