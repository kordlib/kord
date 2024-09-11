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
import dev.kord.gateway.Command
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayCloseCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

/**
 * A Gateway received an event.
 */
public sealed class GatewayEvent : Event

/**
 * The event dispatched when a bot connects to the Gateway.
 */
public class ConnectEvent(
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GatewayEvent()

public sealed class DisconnectEvent : GatewayEvent() {

    /**
     * A Gateway was detached, all resources tied to that gateway should be freed.
     */
    public class DetachEvent(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "DetachEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * The user closed the Gateway connection.
     */
    public class UserCloseEvent(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "UserCloseEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    public class TimeoutEvent(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "TimeoutEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    public class DiscordCloseEvent(
        override val kord: Kord,
        override val shard: Int,
        public val closeCode: GatewayCloseCode,
        public val recoverable: Boolean,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "DiscordCloseEvent(kord=$kord, shard=$shard, closeCode=$closeCode, recoverable=$recoverable)"
        }
    }

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [Gateway.start], otherwise all resources linked to the Gateway should free and the Gateway [detached][Gateway.detach].
     */
    public class RetryLimitReachedEvent(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "RetryLimitReachedEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * Discord requested a reconnect, the gateway will close and attempt to resume the session.
     */
    public class ReconnectingEvent(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "ReconnectingEvent(kord=$kord, shard=$shard)"
        }
    }

    /**
     * The gateway closed and will attempt to start a new session.
     */
    public class SessionReset(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "SessionReset(kord=$kord, shard=$shard)"
        }
    }

    /**
     * Discord is no longer responding to the gateway commands, the connection will be closed and an attempt to resume the session will be made.
     * Any [commands][Command] send recently might not complete, and won't be automatically requeued.
     */
    public class ZombieConnectionEvent(
        override val kord: Kord,
        override val shard: Int,
        override val customContext: Any?,
    ) : DisconnectEvent() {
        override fun toString(): String {
            return "ZombieConnectionEvent(kord=$kord, shard=$shard)"
        }
    }

}

/**
 * The event sent after the bot sends a valid `Identify` payload.
 *
 * @property gatewayVersion The current API version in use
 * @property guildIds A [Set] of guild IDs for guilds the bot is a member of. These guild objects are unavailable to the bot
 * @property self The [User] object for the bot
 * @property sessionId The ID of the session. This is used for resuming connections
 * @property resumeGatewayUrl The URL of the gateway to use for resuming connections
 */
public class ReadyEvent(
    public val gatewayVersion: Int,
    public val guildIds: Set<Snowflake>,
    public val self: User,
    public val sessionId: String,
    public val resumeGatewayUrl: String,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : GatewayEvent(), Strategizable {

    /**
     * A set of all the [Guild]s the bot is in.
     */
    public val guilds: Set<GuildBehavior> get() = guildIds.map { GuildBehavior(it, kord) }.toSet()

    /**
     * Requests to get a flow of all the [Guild]s the bot is in from the [supplier], filtered to those in the [guilds] set.
     *
     * @return A flow of [Guild]s the bot is in
     */
    public fun getGuilds(): Flow<Guild> = supplier.guilds.filter { it.id in guildIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReadyEvent =
        ReadyEvent(gatewayVersion, guildIds, self, sessionId, resumeGatewayUrl, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "ReadyEvent(gatewayVersion=$gatewayVersion, guildIds=$guildIds, self=$self, " +
            "sessionId='$sessionId', resumeGatewayUrl=$resumeGatewayUrl, kord=$kord, shard=$shard, supplier=$supplier)"
}

/**
 * The event sent when the bot is attempting to reconnect.
 */
public class ResumedEvent(
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GatewayEvent() {
    override fun toString(): String {
        return "ResumedEvent(kord=$kord, shard=$shard)"
    }
}
