package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.gateway.ConnectEvent
import com.gitlab.kordlib.core.event.gateway.DisconnectEvent
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.gateway.ResumedEvent
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class LifeCycleEventHandler(
        kord: Kord,
        gateway: Gateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event) = when (event) {
        is Ready -> handle(event)
        is Resumed -> coreEventChannel.send(ResumedEvent(kord))
        SessionClose -> coreEventChannel.send(DisconnectEvent(kord))
        CloseForReconnect -> coreEventChannel.send(DisconnectEvent(kord))
        Reconnect -> coreEventChannel.send(ConnectEvent(kord))
        else -> Unit
    }

    private suspend fun handle(event: Ready) = with(event.data) {
        val guilds = guilds.map { Snowflake(it.id) }.toSet()
        val self = UserData.from(event.data.user)

        cache.put(self)

        coreEventChannel.send(ReadyEvent(event.data.version, guilds, User(self, kord), sessionId, kord))
    }
}