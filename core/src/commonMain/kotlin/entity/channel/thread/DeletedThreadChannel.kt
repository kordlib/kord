package dev.kord.core.entity.channel.thread

import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class DeletedThreadChannel(
    public override val data: ChannelData,
    public override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : MaybeThreadChannel {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DeletedThreadChannel {
        return DeletedThreadChannel(data, kord, strategy.supply(kord))
    }
}
