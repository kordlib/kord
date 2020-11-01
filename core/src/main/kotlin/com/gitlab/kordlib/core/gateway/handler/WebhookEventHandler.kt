package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.WebhookUpdateEvent
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.WebhooksUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import com.gitlab.kordlib.core.event.Event as CoreEvent

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
        coreFlow.emit(WebhookUpdateEvent(Snowflake(guildId), Snowflake(channelId), kord, shard))
    }

}