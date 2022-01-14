package dev.kord.core.entity.interaction

import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildInteractionBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.ApplicationCommandInteractionBehavior
import dev.kord.core.behavior.interaction.AutoCompleteInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * Interaction which contains a contains command data.
 */
public sealed interface ApplicationCommandInteraction : Interaction, ApplicationCommandInteractionBehavior {
    public val invokedCommandId: Snowflake get() = data.data.id.value!!

    public val name: String get() = data.data.name.value!!

}

/**
 * Represents an interaction of type [ApplicationCommand][dev.kord.common.entity.InteractionType.ApplicationCommand]
 */
public sealed interface ApplicationCommandInvocationInteraction : ActionInteraction, ApplicationCommandInteraction {
    public val invokedCommandType: ApplicationCommandType get() = data.data.type.value!!

    public val resolvedObjects: ResolvedObjects?
        get() = data.data.resolvedObjectsData.unwrap {
            ResolvedObjects(it, kord)
        }
}


/**
 * An [ApplicationCommandInteraction] that contains a [command].
 */
public sealed interface ChatInputCommandInteraction : Interaction {
    public val command: InteractionCommand get() = InteractionCommand(data.data, kord)
}

/**
 * An [ApplicationCommandInteraction] that's invoked through chat input.
 */
public sealed interface ChatInputCommandInvocationInteraction : ChatInputCommandInteraction, ApplicationCommandInvocationInteraction


/**
 * A [ApplicationCommandInteraction] that's invoked through chat input specific to a guild.
 */
public class GuildChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInvocationInteraction, GuildApplicationCommandInteraction {
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
) : ChatInputCommandInvocationInteraction, GlobalApplicationCommandInteraction {
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
public sealed interface UserCommandInteraction : ApplicationCommandInvocationInteraction {

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
public sealed interface MessageCommandInteraction : ApplicationCommandInvocationInteraction {

    public val targetId: Snowflake get() = data.data.targetId.value!!

    public val targetBehavior: MessageBehavior get() = MessageBehavior(channelId, targetId, kord)

    public suspend fun getTarget(): Message = supplier.getMessage(channelId, targetId)

    public suspend fun getTargetOrNull(): Message? = supplier.getMessageOrNull(channelId, targetId)

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

/**
 * ActionInteraction indicating an auto-complete request from Discord.
 *
 * **Follow-ups and normals responses don't work on this type**
 *
 * **No matter what argument type is used all arguments will be [CommandArgument.AutoCompleteArgument]s
 *
 * Check [AutoCompleteInteractionBehavior] for response options
 */
public sealed interface AutoCompleteInteraction : AutoCompleteInteractionBehavior, ChatInputCommandInteraction, DataInteraction

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
 * **Follow-ups and normals responses don't work on this type**
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
 * **Follow-ups and normals responses don't work on this type**
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
