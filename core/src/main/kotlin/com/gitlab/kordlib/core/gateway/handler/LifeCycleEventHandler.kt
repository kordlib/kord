package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.gateway.ConnectEvent
import com.gitlab.kordlib.core.event.gateway.DisconnectEvent
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.gateway.ResumedEvent
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class LifeCycleEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is Ready -> handle(event, shard)
        is Resumed -> coreFlow.emit(ResumedEvent(kord, shard))
        Reconnect -> coreFlow.emit(ConnectEvent(kord, shard))
        is Close -> when (event) {
            Close.Detach -> coreFlow.emit(DisconnectEvent.DetachEvent(kord, shard))
            Close.UserClose -> coreFlow.emit(DisconnectEvent.UserCloseEvent(kord, shard))
            Close.Timeout -> coreFlow.emit(DisconnectEvent.TimeoutEvent(kord, shard))
            is Close.DiscordClose -> coreFlow.emit(DisconnectEvent.DiscordCloseEvent(kord, shard, event.closeCode, event.recoverable))
            Close.Reconnecting -> coreFlow.emit(DisconnectEvent.ReconnectingEvent(kord, shard))
            Close.ZombieConnection -> coreFlow.emit(DisconnectEvent.ZombieConnectionEvent(kord, shard))
            Close.RetryLimitReached -> coreFlow.emit(DisconnectEvent.RetryLimitReachedEvent(kord, shard))
            Close.SessionReset -> coreFlow.emit(DisconnectEvent.SessionReset(kord, shard))
        }

        else -> Unit
    }

    private suspend fun handle(event: Ready, shard: Int) = with(event.data) {
        val guilds = guilds.map { it.id }.toSet()
        val self = UserData.from(event.data.user)

        cache.put(self)

        coreFlow.emit(ReadyEvent(event.data.version, guilds, User(self, kord), sessionId, kord, shard))
    }
}