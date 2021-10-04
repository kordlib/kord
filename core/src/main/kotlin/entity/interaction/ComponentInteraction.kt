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

public sealed interface ComponentInteraction : Interaction, ComponentInteractionBehavior {

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

    abstract override fun toString(): String

}

public sealed interface GlobalComponentInteraction : ComponentInteraction

public sealed interface GuildComponentInteraction : ComponentInteraction {

    public val guildId: Snowflake get() = data.guildId.value!!

    public val member: Member get() = Member(data.member.value!!, user.data, kord)
}

public sealed interface ButtonInteraction : ComponentInteraction {
    override val component: ButtonComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<ActionRowComponent>()
            .flatMap { it.buttons }
            .firstOrNull { it.customId == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction
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
): ComponentInteraction = when (val type = data.data.componentType.value) {
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
    ComponentType.ActionRow -> error("Action rows can't have interactions")
    is ComponentType.Unknown -> UnknownComponentInteraction(data, kord, supplier)
    null -> error("Component type was null")
}


/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GlobalButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GlobalComponentInteraction, ButtonInteraction {
    override fun equals(other: Any?): Boolean {
        if (other !is ButtonInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction {
        return GlobalButtonInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GlobalButtonInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}


/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GuildButtonInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GuildComponentInteraction, ButtonInteraction {

    override fun equals(other: Any?): Boolean {
        if (other !is ButtonInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ButtonInteraction {
        return GuildButtonInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GuildButtonInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
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

/**
 * An interaction created from a user interacting with a [SelectMenuComponent].
 */

public sealed interface SelectMenuInteraction : ComponentInteraction {

    /**
     * The selected values, the expected range should between 0 and 25.
     *
     * @see [SelectMenuBuilder.minimumValues]
     * @see [SelectMenuBuilder.maximumValues]
     */
    public val values: List<String> get() = data.data.values.orEmpty()

    override val component: SelectMenuComponent?
        get() = message?.components.orEmpty()
            .filterIsInstance<ActionRowComponent>()
            .flatMap { it.selectMenus }
            .firstOrNull { it.customId == componentId }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction

}

/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GuildSelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GuildComponentInteraction, SelectMenuInteraction {
    override fun equals(other: Any?): Boolean {
        if (other !is SelectMenuInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction {
        return GuildSelectMenuInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GuildSelectMenuInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}


/**
 * An interaction created from a user pressing a [ButtonComponent].
 */

public class GlobalSelectMenuInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : GlobalComponentInteraction, SelectMenuInteraction {
    override fun equals(other: Any?): Boolean {
        if (other !is SelectMenuInteraction) return false

        return id == other.id
    }

    override fun hashCode(): Int = data.hashCode()


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SelectMenuInteraction {
        return GlobalSelectMenuInteraction(data, kord, strategy.supply(kord))
    }

    override fun toString(): String =
        "GlobalSelectMenuInteraction(data=$data, applicationId=$applicationId, kord=$kord, supplier=$supplier, user=$user)"
}
