package dev.kord.core.entity.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.application.MessageCommand
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/** An [ApplicationCommandInteraction] created when a user uses a [MessageCommand]. */
public sealed interface MessageCommandInteraction : ApplicationCommandInteraction {

    /** The id of the message targeted by the [MessageCommand]. */
    public val targetId: Snowflake get() = data.data.targetId.value!!

    @Deprecated("Renamed to 'target'.", ReplaceWith("this.target"), DeprecationLevel.ERROR)
    public val targetBehavior: MessageBehavior
        get() = target

    /** The behavior of the message targeted by the [MessageCommand]. */
    public val target: MessageBehavior get() = MessageBehavior(channelId, targetId, kord)

    public suspend fun getTarget(): Message = supplier.getMessage(channelId, targetId)

    public suspend fun getTargetOrNull(): Message? = supplier.getMessageOrNull(channelId, targetId)

    public val messages: Map<Snowflake, Message> get() = resolvedObjects!!.messages!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageCommandInteraction
}

/** A [MessageCommandInteraction] that took place in the context of a [Guild]. */
public class GuildMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GuildApplicationCommandInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageCommandInteraction =
        GuildMessageCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildMessageCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildMessageCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [MessageCommandInteraction] that took place in a global context (e.g. a DM). */
public class GlobalMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GlobalApplicationCommandInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalMessageCommandInteraction =
        GlobalMessageCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalMessageCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalMessageCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
