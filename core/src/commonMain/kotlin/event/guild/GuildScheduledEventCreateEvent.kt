package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched when a scheduled guild event was created.
 *
 * See [Guild Scheduled Event Create](https://discord.com/developers/docs/topics/gateway-events#guild-scheduled-event-create)
 *
 * @see GuildScheduledEvent
 * @see GuildScheduledEventEvent
 */
public data class GuildScheduledEventCreateEvent(
    override val scheduledEvent: GuildScheduledEvent,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : GuildScheduledEventEvent {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildScheduledEventCreateEvent =
        copy(supplier = strategy.supply(kord))
}
