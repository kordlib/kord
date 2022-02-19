package dev.kord.core.entity.interaction

import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ComponentInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.component.ActionRowComponent
import dev.kord.core.entity.component.Component
import dev.kord.core.entity.component.TextInputComponent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [ActionInteraction] created when a user submits a modal.
 *
 * Can contain a [message].
 */
public sealed interface ModalSubmitInteraction : ActionInteraction, ComponentInteractionBehavior {

    /**
     * The [custom id](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-modal)
     * of the modal.
     */
    public val modalId: String get() = data.data.customId.value!!

    /** The [ActionRowComponent]s of the modal containing the values submitted by the user. */
    public val actionRows: List<ActionRowComponent>
        get() = data.data.components.orEmpty().map { ActionRowComponent(it) }

    /**
     * The [TextInputComponent]s of the modal, indexed by their [customId][TextInputComponent.customId]. They contain
     * the [value][TextInputComponent.value]s submitted by the user.
     */
    public val textInputs: Map<String, TextInputComponent>
        get() = actionRows
            .flatMap { it.components }
            .filterIsInstance<TextInputComponent>()
            .associateBy { it.customId }

    /**
     * The message the [Component], to which the modal was the response for, is attached to.
     *
     * This is only present if the interaction was created by submitting a modal that was the response for a
     * [ComponentInteraction].
     */
    public val message: Message? get() = data.message.unwrap { Message(it, kord) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ModalSubmitInteraction
}

/** A [ModalSubmitInteraction] that took place in the context of a [Guild]. */
public class GuildModalSubmitInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ModalSubmitInteraction, GuildInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildModalSubmitInteraction =
        GuildModalSubmitInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildModalSubmitInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildModalSubmitInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [ModalSubmitInteraction] that took place in a global context (e.g. a DM). */
public class GlobalModalSubmitInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ModalSubmitInteraction, GlobalInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalModalSubmitInteraction =
        GlobalModalSubmitInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalModalSubmitInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalModalSubmitInteraction(data=$data, kord=$kord, supplier=$supplier)"
}


public fun ModalSubmitInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ModalSubmitInteraction = when (data.guildId) {
    is OptionalSnowflake.Missing -> GlobalModalSubmitInteraction(data, kord, supplier)
    is OptionalSnowflake.Value -> GuildModalSubmitInteraction(data, kord, supplier)
}
