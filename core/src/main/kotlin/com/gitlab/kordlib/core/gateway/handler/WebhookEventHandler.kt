package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.WebhookUpdateEvent
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.WebhooksUpdate
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class WebhookEventHandler(
        kord: Kord,
        gateway: Gateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event) = when (event) {
        is WebhooksUpdate -> handle(event)
        else -> Unit
    }

    private suspend fun handle(event: WebhooksUpdate) = with(event.webhooksUpdateData) {
        coreEventChannel.send(WebhookUpdateEvent(Snowflake(guildId), Snowflake(channelId), kord))
    }

}