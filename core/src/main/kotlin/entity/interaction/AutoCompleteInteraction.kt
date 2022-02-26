package dev.kord.core.entity.interaction

import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.AutoCompleteInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A [DataInteraction] indicating an auto-complete request from Discord.
 *
 * **No matter what argument type is used all [focused][CommandArgument.focused] arguments will be
 * [CommandArgument.AutoCompleteArgument]s.**
 *
 * Check [AutoCompleteInteractionBehavior] for response options.
 */
public sealed interface AutoCompleteInteraction : DataInteraction, AutoCompleteInteractionBehavior {

    /**
     * An [InteractionCommand] that contains the values the user filled so far.
     *
     * This might not contain all [options][InteractionCommand.options] and
     * [resolvedObjects][InteractionCommand.resolvedObjects], they will be available in a [ChatInputCommandInteraction].
     */
    public val command: InteractionCommand get() = InteractionCommand(data.data, kord)

    /**
     * The single [focused][OptionValue.focused] option the user is currently typing.
     *
     * This is always a [StringOptionValue], the [value][OptionValue.value] is not validated yet, so it could be
     * anything.
     */
    public val focusedOption: StringOptionValue
        get() = command.options.values.single { it.focused } as StringOptionValue

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoCompleteInteraction
}

internal fun AutoCompleteInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): AutoCompleteInteraction = when (data.guildId) {
    is OptionalSnowflake.Value -> GuildAutoCompleteInteraction(data, kord, supplier)
    is OptionalSnowflake.Missing -> GlobalAutoCompleteInteraction(data, kord, supplier)
}

/** An [AutoCompleteInteraction] that took place in a global context (e.g. a DM). */
public class GlobalAutoCompleteInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : AutoCompleteInteraction, GlobalInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalAutoCompleteInteraction =
        GlobalAutoCompleteInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalAutoCompleteInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalAutoCompleteInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** An [AutoCompleteInteraction] that took place in the context of a [Guild]. */
public class GuildAutoCompleteInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : AutoCompleteInteraction, GuildInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildAutoCompleteInteraction =
        GuildAutoCompleteInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildAutoCompleteInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildAutoCompleteInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
