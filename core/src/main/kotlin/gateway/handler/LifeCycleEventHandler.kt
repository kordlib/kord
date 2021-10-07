package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.event.gateway.ConnectEvent
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class LifeCycleEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is Ready -> handle(event, shard, kord, coroutineScope)
            is Resumed -> ResumedEvent(kord, shard, coroutineScope)
            Reconnect -> ConnectEvent(kord, shard, coroutineScope)
            is Close -> when (event) {
                Close.Detach -> DisconnectEvent.DetachEvent(kord, shard, coroutineScope)
                Close.UserClose -> DisconnectEvent.UserCloseEvent(kord, shard, coroutineScope)
                Close.Timeout -> DisconnectEvent.TimeoutEvent(kord, shard, coroutineScope)
                is Close.DiscordClose -> DisconnectEvent.DiscordCloseEvent(
                    kord,
                    shard,
                    event.closeCode,
                    event.recoverable,
                    coroutineScope
                )
                Close.Reconnecting -> DisconnectEvent.ReconnectingEvent(kord, shard, coroutineScope)
                Close.ZombieConnection -> DisconnectEvent.ZombieConnectionEvent(kord, shard, coroutineScope)
                Close.RetryLimitReached -> DisconnectEvent.RetryLimitReachedEvent(kord, shard, coroutineScope)
                Close.SessionReset -> DisconnectEvent.SessionReset(kord, shard, coroutineScope)
            }
            else -> null
        }

    private suspend fun handle(event: Ready, shard: Int, kord: Kord, coroutineScope: CoroutineScope): ReadyEvent =
        with(event.data) {
            val guilds = guilds.map { it.id }.toSet()
            val self = UserData.from(event.data.user)

            cache.put(self)

            return ReadyEvent(
                event.data.version,
                guilds,
                User(self, kord),
                sessionId,
                kord,
                shard,
                coroutineScope = coroutineScope
            )
        }
}