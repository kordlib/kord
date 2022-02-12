package dev.kord.core.entity.interaction

import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.ApplicationCommandInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * Represents an interaction of type [ApplicationCommand][dev.kord.common.entity.InteractionType.ApplicationCommand]
 */
public sealed interface ApplicationCommandInteraction :  ActionInteraction, ApplicationCommandInteractionBehavior {
    public val invokedCommandType: ApplicationCommandType get() = data.data.type.value!!

    public val resolvedObjects: ResolvedObjects?
        get() = data.data.resolvedObjectsData.unwrap {
            ResolvedObjects(it, kord)
        }
}

/**
 * An [ActionInteraction] that took place in a Global Context with [GlobalApplicationCommand].
 */
public sealed interface GlobalApplicationCommandInteraction : ApplicationCommandInteraction, GlobalInteraction {
    /**
     * The user who invoked the interaction.
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalApplicationCommandInteraction =
        GlobalApplicationCommandInteraction(data, kord, strategy.supply(kord))

    override val applicationId: Snowflake
        get() = super<ApplicationCommandInteraction>.applicationId
}

public fun GlobalApplicationCommandInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GlobalApplicationCommandInteraction {
    return when (data.data.type.value) {
        ApplicationCommandType.ChatInput -> GlobalChatInputCommandInteraction(data, kord, supplier)
        ApplicationCommandType.User -> GlobalUserCommandInteraction(data, kord, supplier)
        ApplicationCommandType.Message -> GlobalMessageCommandInteraction(data, kord, supplier)
        is ApplicationCommandType.Unknown -> error("Unknown interaction.")
        null -> error("No component type was provided")
    }
}

/**
 * An [ActionInteraction] that took place in a Global Context with [dev.kord.core.entity.application.GuildApplicationCommand].
 */


public sealed interface GuildApplicationCommandInteraction : ApplicationCommandInteraction, GuildInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildApplicationCommandInteraction =
        GuildApplicationCommandInteraction(data, kord, strategy.supply(kord))

}

public fun GuildApplicationCommandInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GuildApplicationCommandInteraction {
    return when (data.data.type.value) {
        ApplicationCommandType.ChatInput -> GuildChatInputCommandInteraction(data, kord, supplier)
        ApplicationCommandType.User -> GuildUserCommandInteraction(data, kord, supplier)
        ApplicationCommandType.Message -> GuildMessageCommandInteraction(data, kord, supplier)
        is ApplicationCommandType.Unknown -> error("Unknown interaction.")
        null -> error("No interaction type provided.")
    }
}

public class UnknownApplicationCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ApplicationCommandInteraction {
    override val user: UserBehavior
        get() = UserBehavior(data.user.value!!.id, kord)

    override fun equals(other: Any?): Boolean {
        return if (other !is ApplicationCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UnknownApplicationCommandInteraction {
        return UnknownApplicationCommandInteraction(data, kord, strategy.supply(kord))
    }
}
