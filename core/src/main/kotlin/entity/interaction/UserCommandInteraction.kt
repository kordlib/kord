package dev.kord.core.entity.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.application.UserCommand
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/** An [ApplicationCommandInteraction] created when a user uses a [UserCommand]. */
public sealed interface UserCommandInteraction : ApplicationCommandInteraction {

    /** The id of the user targeted by the [UserCommand]. */
    public val targetId: Snowflake get() = data.data.targetId.value!!

    @Deprecated("Renamed to 'target'.", ReplaceWith("this.target"), DeprecationLevel.ERROR)
    public val targetBehavior: UserBehavior
        get() = target

    /** The behavior of the user targeted by the [UserCommand]. */
    public val target: UserBehavior get() = UserBehavior(targetId, kord)

    public suspend fun getTarget(): User = supplier.getUser(targetId)

    public suspend fun getTargetOrNull(): User? = supplier.getUserOrNull(targetId)

    public val users: Map<Snowflake, User> get() = resolvedObjects!!.users!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UserCommandInteraction
}

/** A [UserCommandInteraction] that took place in the context of a [Guild]. */
public class GuildUserCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : UserCommandInteraction, GuildApplicationCommandInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildUserCommandInteraction =
        GuildUserCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GuildUserCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GuildUserCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}

/** A [GlobalUserCommandInteraction] that took place in a global context (e.g. a DM). */
public class GlobalUserCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : UserCommandInteraction, GlobalApplicationCommandInteraction {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalUserCommandInteraction =
        GlobalUserCommandInteraction(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean = other is GlobalUserCommandInteraction && this.id == other.id
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String =
        "GlobalUserCommandInteraction(data=$data, kord=$kord, supplier=$supplier)"
}
