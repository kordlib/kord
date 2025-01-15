 package dev.kord.core.entity.interaction

import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public sealed interface PrimaryEntryPointCommandInteraction : ApplicationCommandInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PrimaryEntryPointCommandInteraction
}

public class GlobalPrimaryEntryPointCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : PrimaryEntryPointCommandInteraction, GlobalApplicationCommandInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalPrimaryEntryPointCommandInteraction =
        GlobalPrimaryEntryPointCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalPrimaryEntryPointCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalPrimaryCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

public class GuildPrimaryEntryPointCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : PrimaryEntryPointCommandInteraction, GuildApplicationCommandInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildPrimaryEntryPointCommandInteraction =
        GuildPrimaryEntryPointCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalPrimaryEntryPointCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildPrimaryEntryPointInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
