package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.event.gateway.ConnectEvent
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.gateway.*
import dev.kord.core.event.Event as CoreEvent

internal class LifeCycleEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, shard: Int, kord: Kord): CoreEvent? = when (event) {
        is Ready -> handle(event, shard, kord)
        is Resumed -> ResumedEvent(kord, shard)
        Reconnect -> ConnectEvent(kord, shard)
        is Close -> when (event) {
            Close.Detach -> DisconnectEvent.DetachEvent(kord, shard)
            Close.UserClose -> DisconnectEvent.UserCloseEvent(kord, shard)
            Close.Timeout -> DisconnectEvent.TimeoutEvent(kord, shard)
            is Close.DiscordClose -> DisconnectEvent.DiscordCloseEvent(kord, shard, event.closeCode, event.recoverable)
            Close.Reconnecting -> DisconnectEvent.ReconnectingEvent(kord, shard)
            Close.ZombieConnection -> DisconnectEvent.ZombieConnectionEvent(kord, shard)
            Close.RetryLimitReached -> DisconnectEvent.RetryLimitReachedEvent(kord, shard)
            Close.SessionReset -> DisconnectEvent.SessionReset(kord, shard)
        }
        else -> null
    }

    private suspend fun handle(event: Ready, shard: Int, kord: Kord): ReadyEvent = with(event.data) {
        val guilds = guilds.map { it.id }.toSet()
        val self = UserData.from(event.data.user)

        kord.cache.put(self)

        ReadyEvent(event.data.version, guilds, User(self, kord), sessionId, kord, shard)
    }
}
