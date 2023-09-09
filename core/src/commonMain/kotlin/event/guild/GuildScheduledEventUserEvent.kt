package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.entity.*
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a user has [subscribed to][GuildScheduledEventUserAddEvent] or
 * [unsubscribed from][GuildScheduledEventUserRemoveEvent] a [GuildScheduledEvent].
 *
 * See [Guild Scheduled Event](https://discord.com/developers/docs/topics/gateway-events#guild-scheduled-event-user-add)
 */
public sealed interface GuildScheduledEventUserEvent : Event, Strategizable {
    /**
     * The ID of the scheduled guild event.
     */
    public val scheduledEventId: Snowflake

    /**
     * The ID of the user that (un)subscribed from the event.
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


    /**
     * Requests to get the [Member] that (un)subscribed to the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the member was not present
     */
    public suspend fun getMember(): Member = supplier.getMember(guildId, userId)

    /**
     * Requests to get the [Member] that (un)subscribed to the event, or `null` if the member was not present.
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, userId)


    /**
     * Requests to get the [Guild] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the guild was not present
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] that triggered the event, or `null` if the guild was not present.
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)


    /**
     * Requests to get the [GuildScheduledEvent] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the event was not present
     */
    public suspend fun getEvent(): GuildScheduledEvent = supplier.getGuildScheduledEvent(guildId, scheduledEventId)

    /**
     * Requests to get the [GuildScheduledEvent] that triggered the event, or `null` if the event was not present
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getEventOrNull(): GuildScheduledEvent? =
        supplier.getGuildScheduledEventOrNull(guildId, scheduledEventId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventUserEvent
}

/**
 * The event dispatched when a user has subscribed to a [GuildScheduledEvent].
 *
 * See [Guild Scheduled Event User Add](https://discord.com/developers/docs/topics/gateway-events#guild-scheduled-event-user-add)
 */
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

/**
 * The event dispatched when a user has unsubscribed from a [GuildScheduledEvent].
 *
 * See [Guild Scheduled Event User Remove](https://discord.com/developers/docs/topics/gateway-events#guild-scheduled-event-user-remove)
 */
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
