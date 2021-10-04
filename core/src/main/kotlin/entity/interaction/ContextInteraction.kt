package dev.kord.core.entity.interaction

import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.ApplicationCommandInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * Represents an interaction of type [ApplicationCommand][dev.kord.common.entity.InteractionType.ApplicationCommand]
 */
public sealed interface ApplicationCommandInteraction : Interaction, ApplicationCommandInteractionBehavior {
    public val invokedCommandId: Snowflake get() = data.data.id.value!!

    public val name: String get() = data.data.name.value!!

    public val invokedCommandType: ApplicationCommandType get() = data.data.type.value!!

    public val resolvedObjects: ResolvedObjects?
        get() = data.data.resolvedObjectsData.unwrap {
            ResolvedObjects(it, kord)
        }

}


/**
 * An [ApplicationCommandInteraction] that's invoked through chat input.
 */
public sealed interface ChatInputCommandInteraction : ApplicationCommandInteraction {
    public val command: InteractionCommand get() = InteractionCommand(data.data, kord)
}

/**
 * A [ApplicationCommandInteraction] that's invoked through chat input specific to a guild.
 */
public class GuildChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GuildApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GuildChatInputCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id, guildId)
    }
}


/**
 * An [ApplicationCommandInteraction] that's invoked through chat input.
 */
public class GlobalChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GlobalApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GlobalChatInputCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}


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


/**
 * An [ApplicationCommandInteraction] that's invoked through messages.
 */
public sealed interface MessageCommandInteraction : ApplicationCommandInteraction {

    public val targetId: Snowflake get() = data.data.targetId.value!!

    public val targetBehavior: UserBehavior get() = UserBehavior(targetId, kord)

    public suspend fun getTarget(): User = supplier.getUser(targetId)

    public suspend fun getTargetOrNull(): User? = supplier.getUserOrNull(targetId)

    public val messages: Map<Snowflake, Message> get() = resolvedObjects!!.messages!!

}

/**
 * An [ApplicationCommandInteraction] that's invoked through messages specific to a guild.
 */
public class GuildMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GuildApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GuildMessageCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id, guildId)
    }
}

/**
 * An [ApplicationCommandInteraction] that's invoked through messages.
 */
public class GlobalMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GlobalApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GlobalMessageCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
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
