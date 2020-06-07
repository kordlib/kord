package com.gitlab.kordlib.core.event.gateway

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow

sealed class GatewayEvent : Event

class ConnectEvent internal constructor(override val kord: Kord) : GatewayEvent()

class DisconnectEvent internal constructor(override val kord: Kord) : GatewayEvent()

class ReadyEvent internal constructor(
        val gatewayVersion: Int,
        val guildIds: Set<Snowflake>,
        val self: User,
        val sessionId: String,
        override val kord: Kord
) : GatewayEvent() {
    suspend fun getGuilds(): Flow<Guild> = flow {
        for (guildId in guildIds) {
            emit(kord.getGuild(guildId))
        }
    }.filterNotNull()
}

class ResumedEvent(override val kord: Kord) : GatewayEvent()