package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope

/**
 * Event fired when a scheduled event got created.
 *
 * @see GuildScheduledEvent
 * @see GuildScheduledEventEvent
 */
public data class GuildScheduledEventCreateEvent(
    override val scheduledEvent: GuildScheduledEvent,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildScheduledEventEvent, CoroutineScope by coroutineScope {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventCreateEvent =
        copy(supplier = strategy.supply(kord))
}
