package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.event.gateway.ConnectEvent
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.kord.core.event.Event as CoreEvent

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