package dev.kord.core.entity.interaction

import dev.kord.common.entity.ComponentType
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ComponentInteractionBehavior
import dev.kord.core.behavior.interaction.ModalParentInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.component.ButtonComponent
import dev.kord.core.entity.component.Component
import dev.kord.core.entity.component.SelectMenuComponent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [ActionInteraction] created when a user interacts with a [Component].
 *
 * Contains a [message].
 */
public sealed interface ComponentInteraction :
    ActionInteraction,
    ComponentInteractionBehavior,
    ModalParentInteractionBehavior {

    /**
     * The [custom id](https://discord.com/developers/docs/interactions/message-components#custom-id) of the
     * [component].
     */
    public val componentId: String get() = data.data.customId.value!!

    /** The [type][Component.type] of the [component]. */
    public val componentType: ComponentType get() = data.data.componentType.value!!

    /** The [Component] that triggered the interaction. */
    public val component: Component

    /** The message the [component] is attached to. */
    public val message: Message get() = Message(data.message.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ComponentInteraction
}

/** A [ComponentInteraction] that took place in a global context (e.g. a DM). */
public sealed interface GlobalComponentInteraction : ComponentInteraction, GlobalInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalComponentInteraction
}

/** A [ComponentInteraction] that took place in the context of a [Guild]. */
public sealed interface GuildComponentInteraction : ComponentInteraction, GuildInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildComponentInteraction
}


/**
 * Creates a [ComponentInteraction] with the given [data], [kord] and [supplier].
 *
 * @throws Exception if the interaction is not from a [ButtonComponent] or a [SelectMenuComponent].
 */
public fun ComponentInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): ComponentInteraction {
    val inGuild = data.guildId.value != null

    return when (val type = data.data.componentType.value) {
        ComponentType.Button -> when {
            inGuild -> GuildButtonInteraction(data, kord, supplier)
            else -> GlobalButtonInteraction(data, kord, supplier)
        }
        ComponentType.SelectMenu -> when {
            inGuild -> GuildSelectMenuInteraction(data, kord, supplier)
            else -> GlobalSelectMenuInteraction(data, kord, supplier)
        }
        ComponentType.TextInput -> error("Text inputs can't have interactions")
        ComponentType.ActionRow -> error("Action rows can't have interactions")
        is ComponentType.Unknown -> error("Unknown component type: ${type.value}")
        null -> error("Component type was null")
    }
}
