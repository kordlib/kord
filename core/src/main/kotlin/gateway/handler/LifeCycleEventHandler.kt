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

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is Ready -> handle(event, shard, kord, context)
            is Resumed -> ResumedEvent(kord, shard, context?.get())
            Reconnect -> ConnectEvent(kord, shard, context?.get())
            is Close -> when (event) {
                Close.Detach -> DisconnectEvent.DetachEvent(kord, shard, context?.get())
                Close.UserClose -> DisconnectEvent.UserCloseEvent(kord, shard, context?.get())
                Close.Timeout -> DisconnectEvent.TimeoutEvent(kord, shard, context?.get())
                is Close.DiscordClose -> DisconnectEvent.DiscordCloseEvent(
                    kord,
                    shard,
                    event.closeCode,
                    event.recoverable,
                    context?.get(),
                )
                Close.Reconnecting -> DisconnectEvent.ReconnectingEvent(kord, shard, context?.get())
                Close.ZombieConnection -> DisconnectEvent.ZombieConnectionEvent(kord, shard, context?.get())
                Close.RetryLimitReached -> DisconnectEvent.RetryLimitReachedEvent(kord, shard, context?.get())
                Close.SessionReset -> DisconnectEvent.SessionReset(kord, shard, context?.get())
            }
            else -> null
        }

    private suspend fun handle(event: Ready, shard: Int, kord: Kord, context: LazyContext?): ReadyEvent =
        with(event.data) {
            val guilds = guilds.map { it.id }.toSet()
            val self = UserData.from(event.data.user)

            kord.cache.put(self)

            ReadyEvent(
                event.data.version,
                guilds,
                User(self, kord),
                sessionId,
                kord,
                shard,
                context?.get(),
            )
        }
}
