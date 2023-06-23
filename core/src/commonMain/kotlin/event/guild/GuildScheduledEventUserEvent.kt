package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.*
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * Sent when a user has [subscribed to][GuildScheduledEventUserAddEvent] or
 * [unsubscribed from][GuildScheduledEventUserRemoveEvent] a [GuildScheduledEvent].
 */
public sealed interface GuildScheduledEventUserEvent : Event, Strategizable {
    public val scheduledEventId: Snowflake
    public val userId: Snowflake
    public val guildId: Snowflake

    public suspend fun getUser(): User = supplier.getUser(userId)
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

/** Sent when a user has subscribed to a [GuildScheduledEvent]. */
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

/** Sent when a user has unsubscribed from a [GuildScheduledEvent]. */
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
