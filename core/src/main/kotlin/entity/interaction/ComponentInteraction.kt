package dev.kord.core.entity.interaction

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ComponentInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Member
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

public sealed interface ComponentInteraction : ActionInteraction, ComponentInteractionBehavior {

    override val user: User
        get() = User(data.user.value!!, kord)


    /**
     * The message that contains the interacted component, null if the message is ephemeral.
     */
    public val message: Message?
        get() = data.message.unwrap { Message(it, kord, supplier) }

    /**
     * The [ButtonComponent.customId] or [SelectMenuComponent.customId] that triggered the interaction.
     */
    public val componentId: String get() = data.data.customId.value!!

    public val componentType: ComponentType get() = data.data.componentType.value!!

    /**
     * The [Component] the user interacted with, null if the message is ephemeral.
     */
    public val component: Component?

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): ComponentInteraction


}

public sealed interface GlobalComponentInteraction : ComponentInteraction, GlobalInteraction {
    override val user: User
        get() = super<GlobalInteraction>.user
}

public sealed interface GuildComponentInteraction : ComponentInteraction, GuildInteraction {
    override val user: Member
        get() = super<GuildInteraction>.user
}


/**
 * Creates a [ComponentInteraction] with the given [data], [applicationId], [kord] and [supplier].
 *
 * @throws IllegalArgumentException if the interaction is not from a [ButtonComponent] or a [SelectMenuComponent].
 */

public fun ComponentInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): ComponentInteraction = when (data.data.componentType.value) {
    ComponentType.Button -> if (data.guildId.value == null) GlobalButtonInteraction(
        data,
        kord,
        supplier
    ) else GuildButtonInteraction(data, kord, supplier)
    ComponentType.SelectMenu -> if (data.guildId.value == null) GlobalSelectMenuInteraction(
        data,
        kord,
        supplier
    ) else GuildSelectMenuInteraction(data, kord, supplier)
    ComponentType.TextInput -> error("Text inputs can't have interactions")
    ComponentType.ActionRow -> error("Action rows can't have interactions")
    is ComponentType.Unknown -> UnknownComponentInteraction(data, kord, supplier)
    null -> error("Component type was null")
}


public class UnknownComponentInteraction(
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
