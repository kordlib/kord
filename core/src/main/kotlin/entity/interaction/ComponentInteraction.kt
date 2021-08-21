package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.ComponentInteractionBehavior
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.component.*
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.component.SelectMenuBuilder

/**
 * An interaction created from a user interaction with a [Component].
 *
 * @see ButtonInteraction
 * @see SelectMenuInteraction
 */

sealed interface ComponentInteraction : Interaction, ComponentInteractionBehavior {

    override val user: User
        get() = User(data.user.value!!, kord)


    /**
     * The message that contains the interacted component, null if the message is ephemeral.
     */
    val message: Message?
        get() = data.message.unwrap { Message(it, kord, supplier) }

    /**
     * The [ButtonComponent.customId] or [SelectMenuComponent.customId] that triggered the interaction.
     */
    val componentId: String get() = data.data.customId.value!!

    /**
     * The [Component] the user interacted with, null if the message is ephemeral.
     */
    val component: Component?

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): ComponentInteraction

    abstract override fun toString(): String

}

/**
 * Creates a [ComponentInteraction] with the given [data], [applicationId], [kord] and [supplier].
 *
 * @throws IllegalArgumentException if the interaction is not from a [ButtonComponent] or a [SelectMenuComponent].
 */

fun ComponentInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): ComponentInteraction = when (val type = data.data.componentType.value) {
    ComponentType.Button -> ButtonInteraction(data, kord, supplier)
    ComponentType.SelectMenu -> SelectMenuInteraction(data, kord, supplier)
    ComponentType.ActionRow -> error("Action rows can't have interactions")
    is ComponentType.Unknown -> UnknownComponentInteraction(data, kord, supplier)
    null -> error("Component type was null")
}


/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

class ButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ComponentInteraction {

    override val component: ButtonComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<ActionRowComponent>()
            .flatMap { it.buttons }
            .firstOrNull { it.customId == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction {
        return ButtonInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "ButtonInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"


    override fun equals(other: Any?): Boolean {
        if (other !is Interaction) return false

        return other.data == data
    }

    override fun hashCode(): Int = data.hashCode()
}



class UnknownComponentInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ComponentInteraction {
    override val component: UnknownComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<UnknownComponent>()
            .firstOrNull { it.data.customId.value == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UnknownComponentInteraction {
        return UnknownComponentInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String {
        return "UnknownComponentInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
    }
}

/**
 * An interaction created from a user interacting with a [SelectMenuComponent].
 */

class SelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ComponentInteraction {

    /**
     * The selected values, the expected range should between 0 and 25.
     *
     * @see [SelectMenuBuilder.minimumValues]
     * @see [SelectMenuBuilder.maximumValues]
     */
    val values: List<String> get() = data.data.values.orEmpty()

    override val component: SelectMenuComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<ActionRowComponent>()
            .flatMap { it.selectMenus }
            .firstOrNull { it.customId == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction {
        return SelectMenuInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "SelectMenuInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"


    override fun equals(other: Any?): Boolean {
        if (other !is Interaction) return false

        return other.data == data
    }

    override fun hashCode(): Int = data.hashCode()

}

