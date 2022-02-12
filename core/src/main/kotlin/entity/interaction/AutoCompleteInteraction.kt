package dev.kord.core.entity.interaction

import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.AutoCompleteInteractionBehavior
import dev.kord.core.behavior.interaction.GuildInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * ActionInteraction indicating an auto-complete request from Discord.
 *
 * **Followups and normals responses don't work on this type**
 *
 * **No matter what argument type is used all [focused][CommandArgument.focused] arguments will be [CommandArgument.AutoCompleteArgument]s**
 *
 * Check [AutoCompleteInteractionBehavior] for response options
 */
public sealed interface AutoCompleteInteraction : AutoCompleteInteractionBehavior, DataInteraction

internal fun AutoCompleteInteraction(
    data: InteractionData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): AutoCompleteInteraction = when (data.guildId) {
    is OptionalSnowflake.Value -> GuildAutoCompleteInteraction(
        data, kord, supplier
    )
    else -> GlobalAutoCompleteInteraction(data, kord, supplier)
}

/**
 * ActionInteraction indicating an auto-complete request from Discord.
 *
 * **Followups and normals responses don't work on this type**
 *
 * @see ApplicationCommandInteraction
 */
public class GlobalAutoCompleteInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : AutoCompleteInteraction, GlobalInteraction {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalAutoCompleteInteraction =
        GlobalAutoCompleteInteraction(data, kord, strategy.supply(kord))
}

/**
 * ActionInteraction indicating an auto-complete request from Discord on a guild.
 *
 * **Followups and normals responses don't work on this type**
 *
 * @see ApplicationCommandInteraction
 */
public class GuildAutoCompleteInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : AutoCompleteInteraction, GuildInteractionBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!

    override val user: User get() = User(data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction =
        GuildAutoCompleteInteraction(data, kord, strategy.supply(kord))
}
