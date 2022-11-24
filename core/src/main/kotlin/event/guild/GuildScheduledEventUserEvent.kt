package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.*
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a user has [subscribed to][GuildScheduledEventUserAddEvent] or
 * [unsubscribed from][GuildScheduledEventUserRemoveEvent] a [GuildScheduledEvent].
 */
public sealed interface GuildScheduledEventUserEvent : Event, Strategizable {
    /**
     * The ID of the scheduled guild event
     */
    public val scheduledEventId: Snowflake

    /**
     * The ID of the user that (un)subscribed from the event
     */
    public val userId: Snowflake

    /**
     * The ID of the guild this event is in.
     */
    public val guildId: Snowflake

    /**
     * Requests to get the [User] that (un)subscribed to the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the user was not present
     */
    public suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the [User] that (un)subscribed to the event, or `null` if the user wasn't present.
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    public suspend fun getMember(): Member = supplier.getMember(guildId, userId)
    public suspend fun getMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, userId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getEvent(): GuildScheduledEvent = supplier.getGuildScheduledEvent(guildId, scheduledEventId)
    public suspend fun getEventOrNull(): GuildScheduledEvent? =
        supplier.getGuildScheduledEventOrNull(guildId, scheduledEventId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventUserEvent
}

/** The event dispatched when a user has subscribed to a [GuildScheduledEvent]. */
public data class GuildScheduledEventUserAddEvent(
    override val scheduledEventId: Snowflake,
    override val userId: Snowflake,
    override val guildId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : GuildScheduledEventUserEvent {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventUserAddEvent =
        GuildScheduledEventUserAddEvent(scheduledEventId, userId, guildId, kord, shard, customContext, strategy.supply(kord))
}

/** The event dispatched when a user has unsubscribed from a [GuildScheduledEvent]. */
public data class GuildScheduledEventUserRemoveEvent(
    override val scheduledEventId: Snowflake,
    override val userId: Snowflake,
    override val guildId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : GuildScheduledEventUserEvent {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventUserRemoveEvent =
        GuildScheduledEventUserRemoveEvent(scheduledEventId, userId, guildId, kord, shard, customContext, strategy.supply(kord))
}
