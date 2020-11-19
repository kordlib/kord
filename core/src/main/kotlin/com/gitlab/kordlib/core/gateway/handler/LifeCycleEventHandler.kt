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
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class LifeCycleEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is Ready -> handle(event, shard)
        is Resumed -> coreEventChannel.send(ResumedEvent(kord, shard))
        Reconnect -> coreEventChannel.send(ConnectEvent(kord, shard))
        is Close -> when (event) {
            Close.Detach -> coreEventChannel.send(DisconnectEvent.DetachEvent(kord, shard))
            Close.UserClose -> coreEventChannel.send(DisconnectEvent.UserCloseEvent(kord, shard))
            Close.Timeout -> coreEventChannel.send(DisconnectEvent.TimeoutEvent(kord, shard))
            is Close.DiscordClose -> coreEventChannel.send(DisconnectEvent.DiscordCloseEvent(kord, shard, event.closeCode, event.recoverable))
            Close.Reconnecting -> coreEventChannel.send(DisconnectEvent.ReconnectingEvent(kord, shard))
            Close.ZombieConnection -> coreEventChannel.send(DisconnectEvent.ZombieConnectionEvent(kord, shard))
            Close.RetryLimitReached -> coreEventChannel.send(DisconnectEvent.RetryLimitReachedEvent(kord, shard))
            Close.SessionReset -> coreEventChannel.send(DisconnectEvent.SessionReset(kord, shard))
        }

        else -> Unit
    }

    private suspend fun handle(event: Ready, shard: Int) = with(event.data) {
        val guilds = guilds.map { it.id }.toSet()
        val self = UserData.from(event.data.user)

        cache.put(self)

        coreEventChannel.send(ReadyEvent(event.data.version, guilds, User(self, kord), sessionId, kord, shard))
    }
}