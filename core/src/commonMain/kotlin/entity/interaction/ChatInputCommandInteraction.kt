package dev.kord.core.entity.interaction

import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.application.ChatInputCommandCommand
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [ApplicationCommandInteraction] created when a user uses a [ChatInputCommandCommand].
 *
 * Contains an [InteractionCommand].
 */
public sealed interface ChatInputCommandInteraction : ApplicationCommandInteraction {

    /** An [InteractionCommand] that contains the values the user submitted when using a [ChatInputCommandCommand]. */
    public val command: InteractionCommand get() = InteractionCommand(data.data, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChatInputCommandInteraction
}

/** A [ChatInputCommandInteraction] that took place in the context of a [Guild]. */
public class GuildChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GuildApplicationCommandInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildChatInputCommandInteraction =
        GuildChatInputCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildChatInputCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildChatInputCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [ChatInputCommandInteraction] that took place in a global context (e.g. a DM). */
public class GlobalChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GlobalApplicationCommandInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalChatInputCommandInteraction =
        GlobalChatInputCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalChatInputCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalChatInputCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
