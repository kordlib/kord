package dev.kord.core.entity.interaction

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.component.ActionRowComponent
import dev.kord.core.entity.component.Component
import dev.kord.core.event.interaction.GlobalModalSubmitInteractionCreateEvent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public sealed interface ModalSubmitInteraction : Interaction, ActionInteraction {
    public val customId: String get() = data.data.customId.value!!
    public val components: List<ActionRowComponent> get() = data.data.components.orEmpty().map { ActionRowComponent(it) }
}

public class GuildModalSubmitInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildInteraction, ModalSubmitInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction {
        return GuildModalSubmitInteraction(data, kord, strategy.supply(kord))
    }
}

public class GlobalModalSubmitInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier

) : GlobalInteraction, ModalSubmitInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalModalSubmitInteraction {
        return GlobalModalSubmitInteraction(data, kord, strategy.supply(kord))
    }
}

public fun ModalSubmitInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ModalSubmitInteraction = when (data.guildId) {
    is OptionalSnowflake.Missing -> GlobalModalSubmitInteraction(data, kord, supplier)
    else -> GuildModalSubmitInteraction(data, kord, supplier)
}
