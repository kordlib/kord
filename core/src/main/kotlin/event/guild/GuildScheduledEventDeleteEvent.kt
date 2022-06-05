package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.CoroutineScope

/**
 * Event fired when a scheduled event got deleted.
 * Use [GuildScheduledEvent.status] to know why the event got deleted.
 *
 * @see GuildScheduledEvent
 * @see GuildScheduledEvent
 */
public data class GuildScheduledEventDeleteEvent(
    public override val scheduledEvent: GuildScheduledEvent,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildScheduledEventEvent, CoroutineScope by coroutineScope {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventDeleteEvent =
        copy(supplier = strategy.supply(kord))
}
