package com.gitlab.kordlib.core.event.gateway

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

sealed class GatewayEvent : Event

class ConnectEvent(override val kord: Kord) : GatewayEvent()

class DisconnectEvent(override val kord: Kord) : GatewayEvent()

class ReadyEvent(
        val gatewayVersion: Int,
        val guildIds: Set<Snowflake>,
        val self: User,
        val sessionId: String,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : GatewayEvent(), Strategizable {

    suspend fun getGuilds(): Flow<Guild> = supplier.guilds.filter { it.id in guildIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReadyEvent =
            ReadyEvent(gatewayVersion, guildIds, self, sessionId, kord, strategy.supply(kord))
}

class ResumedEvent(override val kord: Kord) : GatewayEvent()