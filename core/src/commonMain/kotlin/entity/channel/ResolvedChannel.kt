package dev.kord.core.entity.channel

import dev.kord.common.entity.Permissions
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class ResolvedChannel(
    override val data: ChannelData,
    override val kord: Kord,
    public val strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) : Channel {


    public val name: String get() = data.name.value!!

    public val permissions: Permissions get() = data.permissions.value!!

    override suspend fun asChannel(): Channel = this

    override suspend fun asChannelOrNull(): Channel = this

    override val supplier: EntitySupplier
        get() = strategy.supply(kord)

    override fun toString(): String {
        return "ResolvedChannel(id=$id, type=$type, name=$name, permissions=$permissions)"
    }

}
