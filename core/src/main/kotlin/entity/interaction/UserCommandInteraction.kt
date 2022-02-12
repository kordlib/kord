package dev.kord.core.entity.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import java.util.*


/**
 * An [ApplicationCommandInteraction] that's invoked through user commands.
 */
public sealed interface UserCommandInteraction : ApplicationCommandInteraction {

    public val targetId: Snowflake get() = data.data.targetId.value!!

    public val targetBehavior: UserBehavior get() = UserBehavior(targetId, kord)

    public suspend fun getTarget(): User = supplier.getUser(targetId)

    public suspend fun getTargetOrNull(): User? = supplier.getUserOrNull(targetId)

    public val users: Map<Snowflake, User> get() = resolvedObjects!!.users!!
}

/**
 * An [ApplicationCommandInteraction] that's invoked through user commands specific to a guild.
 */
public class GuildUserCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : UserCommandInteraction, GuildApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GuildUserCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id, guildId)
    }
}

/**
 * An [ApplicationCommandInteraction] that's invoked through user commands.
 */
public class GlobalUserCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : UserCommandInteraction, GlobalApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GlobalUserCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}

