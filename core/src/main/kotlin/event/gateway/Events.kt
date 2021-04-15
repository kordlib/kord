package dev.kord.core.event.gateway

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Close
import dev.kord.gateway.Command
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayCloseCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

sealed class GatewayEvent : Event

class ConnectEvent(override val kord: Kord, override val shard: Int) : GatewayEvent()

sealed class DisconnectEvent : GatewayEvent() {

    /**
     * A Gateway was detached, all resources tied to that gateway should be freed.
     */
    class DetachEvent(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "DetachEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * The user closed the Gateway connection.
     */
    class UserCloseEvent(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "UserCloseEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    class TimeoutEvent(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "TimeoutEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    class DiscordCloseEvent(
        override val kord: Kord,
        override val shard: Int,
        val closeCode: GatewayCloseCode,
        val recoverable: Boolean
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "DiscordCloseEvent(kord=$kord, shard=$shard, closeCode=$closeCode, recoverable=$recoverable)"
        }
    }

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [Gateway.start], otherwise all resources linked to the Gateway should free and the Gateway [detached][Gateway.detach].
     */
    class RetryLimitReachedEvent(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "RetryLimitReachedEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * Discord requested a reconnect, the gateway will close and attempt to resume the session.
     */
    class ReconnectingEvent(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "ReconnectingEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * The gateway closed and will attempt to start a new session.
     */
    class SessionReset(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "SessionReset(kord=$kord, shard=$shard)"
        }
    }

    /**
     * Discord is no longer responding to the gateway commands, the connection will be closed and an attempt to resume the session will be made.
     * Any [commands][Command] send recently might not complete, and won't be automatically requeued.
     */
    class ZombieConnectionEvent(override val kord: Kord, override val shard: Int) : DisconnectEvent() {
        override fun toString(): String {
            return "ZombieConnectionEvent(kord=$kord, shard=$shard)"
        }
    }

}

class ReadyEvent(
    val gatewayVersion: Int,
    val guildIds: Set<Snowflake>,
    val self: User,
    val sessionId: String,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : GatewayEvent(), Strategizable {

    val guilds: Set<GuildBehavior> get() = guildIds.map { GuildBehavior(it, kord) }.toSet()

    suspend fun getGuilds(): Flow<Guild> = supplier.guilds.filter { it.id in guildIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReadyEvent =
        ReadyEvent(gatewayVersion, guildIds, self, sessionId, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "ReadyEvent(gatewayVersion=$gatewayVersion, guildIds=$guildIds, self=$self, sessionId='$sessionId', kord=$kord, shard=$shard, supplier=$supplier)"
    }
}

class ResumedEvent(override val kord: Kord, override val shard: Int) : GatewayEvent() {
    override fun toString(): String {
        return "ResumedEvent(kord=$kord, shard=$shard)"
    }
}