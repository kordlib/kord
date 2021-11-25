package dev.kord.core.event.guild

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope

/**
 * NOTE: this event is currently experimental and not officially supported
 */
@KordPreview
public interface GuildScheduledEventUserEvent : Event, Strategizable {
    public val scheduledEventId: Snowflake
    public val userId: Snowflake
    public val guildId: Snowflake

    public suspend fun getUser(): User = supplier.getUser(userId)
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getEvent(): GuildScheduledEvent = supplier.getGuildScheduledEvent(guildId, scheduledEventId)
    public suspend fun getEventOrNull(): GuildScheduledEvent? =
        supplier.getGuildScheduledEventOrNull(guildId, scheduledEventId)
}

/**
 * NOTE: this event is currently experimental and not officially supported
 */
@KordPreview
public data class GuildScheduledEventUserAddEvent(
    override val scheduledEventId: Snowflake,
    override val userId: Snowflake,
    override val guildId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord),
) : GuildScheduledEventUserEvent, CoroutineScope by coroutineScope {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable = GuildScheduledEventUserAddEvent(
        scheduledEventId, userId, guildId, kord, shard, strategy.supply(kord)
    )
}

/**
 * NOTE: this event is currently experimental and not officially supported
 */
@KordPreview
public data class GuildScheduledEventUserRemoveEvent(
    override val scheduledEventId: Snowflake,
    override val userId: Snowflake,
    override val guildId: Snowflake,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord),
) : GuildScheduledEventUserEvent, CoroutineScope by coroutineScope {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable = GuildScheduledEventUserAddEvent(
        scheduledEventId, userId, guildId, kord, shard, strategy.supply(kord)
    )
}
