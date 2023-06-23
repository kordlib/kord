package dev.kord.core.entity.interaction

import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ApplicationCommandInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/** An [ActionInteraction] created when a user uses an [ApplicationCommand]. */
public sealed interface ApplicationCommandInteraction : ActionInteraction, ApplicationCommandInteractionBehavior {

    /** The id of the invoked command. */
    public val invokedCommandId: Snowflake get() = data.data.id.value!!

    /** The name of the invoked command. */
    public val invokedCommandName: String get() = data.data.name.value!!

    /** The type of the invoked command. */
    public val invokedCommandType: ApplicationCommandType get() = data.data.type.value!!

    /**
     * The id of the guild the invoked command is registered to, `null` if the command is global.
     *
     * This is unrelated to the difference between [GlobalInteraction]s and [GuildInteraction]s, a global command can
     * produce both [GlobalInteraction]s and [GuildInteraction]s.
     */
    public val invokedCommandGuildId: Snowflake? get() = data.data.guildId.value

    public val resolvedObjects: ResolvedObjects?
        get() = data.data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ApplicationCommandInteraction
}


/** An [ApplicationCommandInteraction] that took place in a global context (e.g. a DM). */
public sealed interface GlobalApplicationCommandInteraction : ApplicationCommandInteraction, GlobalInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalApplicationCommandInteraction =
        GlobalApplicationCommandInteraction(data, kord, strategy.supply(kord))
}

public fun GlobalApplicationCommandInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GlobalApplicationCommandInteraction {
    return when (val type = data.data.type.value) {
        ApplicationCommandType.ChatInput -> GlobalChatInputCommandInteraction(data, kord, supplier)
        ApplicationCommandType.User -> GlobalUserCommandInteraction(data, kord, supplier)
        ApplicationCommandType.Message -> GlobalMessageCommandInteraction(data, kord, supplier)
        is ApplicationCommandType.Unknown -> error("Unknown application command type ${type.value}")
        null -> error("No application command type was provided")
    }
}


/** An [ApplicationCommandInteraction] that took place in the context of a [Guild]. */
public sealed interface GuildApplicationCommandInteraction : ApplicationCommandInteraction, GuildInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildApplicationCommandInteraction =
        GuildApplicationCommandInteraction(data, kord, strategy.supply(kord))
}

public fun GuildApplicationCommandInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GuildApplicationCommandInteraction {
    return when (val type = data.data.type.value) {
        ApplicationCommandType.ChatInput -> GuildChatInputCommandInteraction(data, kord, supplier)
        ApplicationCommandType.User -> GuildUserCommandInteraction(data, kord, supplier)
        ApplicationCommandType.Message -> GuildMessageCommandInteraction(data, kord, supplier)
        is ApplicationCommandType.Unknown -> error("Unknown application command type ${type.value}")
        null -> error("No application command type was provided")
    }
}
