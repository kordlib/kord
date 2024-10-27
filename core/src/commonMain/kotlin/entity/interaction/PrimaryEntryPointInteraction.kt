 package dev.kord.core.entity.interaction

import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public sealed interface PrimaryEntryPointInteraction : ApplicationCommandInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PrimaryEntryPointInteraction
}

public class GlobalPrimaryEntryPointInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : PrimaryEntryPointInteraction, GlobalApplicationCommandInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalPrimaryEntryPointInteraction =
        GlobalPrimaryEntryPointInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalPrimaryEntryPointInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalPrimaryCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

public class GuildPrimaryEntryPointInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : PrimaryEntryPointInteraction, GuildApplicationCommandInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildPrimaryEntryPointInteraction =
        GuildPrimaryEntryPointInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalPrimaryEntryPointInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildPrimaryEntryPointInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
