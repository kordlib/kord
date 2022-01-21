package dev.kord.core.entity.interaction

import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.CommandArgument
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.mapValues
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildInteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.cache.data.ApplicationInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.ResolvedObjectsData
import dev.kord.core.entity.*
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of [ActionInteraction](https://discord.com/developers/docs/interactions/slash-commands#interaction) which does perform an action
 * (e.g. slash commands and context actions).
 *
 * @see DataInteraction
 * @see Interaction
 */
public sealed interface ActionInteraction : Interaction, ActionInteractionBehavior

/**
 * The base interaction for all slash-command related interactions.
 *
 * @see GuildApplicationCommandInteraction
 */
public sealed class CommandInteraction : ActionInteraction {
    public val command: InteractionCommand
        get() = InteractionCommand(data.data, kord)
}

/**
 * The base command of all commands that can be executed under an interaction event.
 */
public sealed interface InteractionCommand : KordObject {
    /**
     * The id of the root command.
     */
    public val rootId: Snowflake

    /**
     * The root command name
     */
    public val rootName: String

    /**
     * the values passed to the command.
     */
    public val options: Map<String, OptionValue<*>>

    public val resolved: ResolvedObjects?

    public val strings: Map<String, String> get() = filterOptions()

    public val integers: Map<String, Long> get() = filterOptions()

    public val numbers: Map<String, Double> get() = filterOptions()

    public val booleans: Map<String, Boolean> get() = filterOptions()

    public val users: Map<String, User> get() = filterOptions()

    public val members: Map<String, Member> get() = filterOptions()

    public val channels: Map<String, ResolvedChannel> get() = filterOptions()

    public val roles: Map<String, Role> get() = filterOptions()

    public val mentionables: Map<String, Entity> get() = filterOptions()

    private inline fun <reified T> filterOptions(): Map<String, T> {
        return buildMap {
            options.onEach { (key, value) ->
                val wrappedValue = value.value
                if (wrappedValue is T) put(key, wrappedValue)
            }
        }
    }
}

public fun InteractionCommand(
    data: ApplicationInteractionData,
    kord: Kord
): InteractionCommand {
    val firstLevelOptions = data.options.orEmpty()
    val rootPredicate = firstLevelOptions.isEmpty() || firstLevelOptions.any { it.value.value != null }
    val groupPredicate = firstLevelOptions.any { it.subCommands.orEmpty().isNotEmpty() }
    val subCommandPredicate =
        firstLevelOptions.all { it.value is Optional.Missing && it.subCommands is Optional.Missing }

    return when {
        rootPredicate -> RootCommand(data, kord)
        groupPredicate -> GroupCommand(data, kord)
        subCommandPredicate -> SubCommand(data, kord)
        else -> error("The interaction data provided is not an chat input command")
    }
}

/**
 * Represents an invocation of a root command.
 *
 * The root command is the first command defined in a slash-command structure.
 */

public class RootCommand(
    public val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand {

    override val rootId: Snowflake
        get() = data.id.value!!

    override val rootName: String get() = data.name.value!!

    override val options: Map<String, OptionValue<*>>
        get() = data.options.orEmpty()
            .associate { it.name to OptionValue(it.value.value!!, resolved) }

    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

}

/**
 * Represents an invocation of a sub-command under the [RootCommand]
 */

public class SubCommand(
    public val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand {

    private val subCommandData = data.options.orEmpty().first()

    override val rootName: String get() = data.name.value!!

    override val rootId: Snowflake
        get() = data.id.value!!

    /**
     * Name of the sub-command executed.
     */
    public val name: String get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.values.orEmpty()
            .associate { it.name to OptionValue(it, resolved) }


    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }


}

/**
 * Represents an invocation of a sub-command under a group.
 */

public class GroupCommand(
    public val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand {

    private val groupData get() = data.options.orEmpty().first()
    private val subCommandData get() = groupData.subCommands.orEmpty().first()

    override val rootId: Snowflake
        get() = data.id.value!!

    override val rootName: String get() = data.name.value!!

    /**
     * Name of the group of this sub-command.
     */
    public val groupName: String get() = groupData.name

    /**
     * Name of this sub-command
     */
    public val name: String get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.options.orEmpty()
            .associate { it.name to OptionValue(it, resolved) }


    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

}


public class ResolvedObjects(
    public val data: ResolvedObjectsData,
    public val kord: Kord,
    public val strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) {
    public val channels: Map<Snowflake, ResolvedChannel>?
        get() = data.channels.mapValues { ResolvedChannel(it.value, kord, strategy) }.value

    public val roles: Map<Snowflake, Role>? get() = data.roles.mapValues { Role(it.value, kord) }.value

    public val users: Map<Snowflake, User>? get() = data.users.mapValues { User(it.value, kord) }.value

    public val members: Map<Snowflake, Member>?
        get() = data.members.mapValues { Member(it.value, users!![it.key]!!.data, kord) }.value

    public val messages: Map<Snowflake, Message>?
        get() = data.messages.mapValues { Message(it.value, kord) }.value

}


public sealed class OptionValue<out T>(public val value: T, public val focused: Boolean) {

    public class RoleOptionValue(value: Role, focused: Boolean) : OptionValue<Role>(value, focused) {
        override fun toString(): String = "RoleOptionValue(value=$value)"
    }

    public open class UserOptionValue(value: User, focused: Boolean) : OptionValue<User>(value, focused) {
        override fun toString(): String = "UserOptionValue(value=$value)"
    }

    public class MemberOptionValue(value: Member, focused: Boolean) : UserOptionValue(value, focused) {
        override fun toString(): String = "MemberOptionValue(value=$value)"
    }

    public class ChannelOptionValue(value: ResolvedChannel, focused: Boolean) :
        OptionValue<ResolvedChannel>(value, focused) {
        override fun toString(): String = "ChannelOptionValue(value=$value)"
    }

    public class IntOptionValue(value: Long, focused: Boolean) : OptionValue<Long>(value, focused) {
        override fun toString(): String = "IntOptionValue(value=$value)"
    }


    public class NumberOptionValue(value: Double, focused: Boolean) : OptionValue<Double>(value, focused) {
        override fun toString(): String = "DoubleOptionValue(value=$value)"
    }

    public class StringOptionValue(value: String, focused: Boolean) : OptionValue<String>(value, focused) {
        override fun toString(): String = "StringOptionValue(value=$value)"
    }

    public class BooleanOptionValue(value: Boolean, focused: Boolean) : OptionValue<Boolean>(value, focused) {
        override fun toString(): String = "BooleanOptionValue(value=$value)"
    }

    public class MentionableOptionValue(value: Entity, focused: Boolean) : OptionValue<Entity>(value, focused) {
        override fun toString(): String = "MentionableOptionValue(value=$value)"
    }

}


public fun OptionValue(value: CommandArgument<*>, resolvedObjects: ResolvedObjects?): OptionValue<*> {
    val focused = value.focused.orElse(false)
    return when (value) {
        is CommandArgument.NumberArgument -> OptionValue.NumberOptionValue(value.value, focused)
        is CommandArgument.BooleanArgument -> OptionValue.BooleanOptionValue(value.value, focused)
        is CommandArgument.IntegerArgument -> OptionValue.IntOptionValue(value.value, focused)
        is CommandArgument.StringArgument, is CommandArgument.AutoCompleteArgument -> OptionValue.StringOptionValue(
            value.value as String,
            focused
        )
        is CommandArgument.ChannelArgument -> {
            val channel = resolvedObjects?.channels.orEmpty()[value.value]
            requireNotNull(channel) { "channel expected for $value but was missing" }

            OptionValue.ChannelOptionValue(channel, focused)
        }

        is CommandArgument.MentionableArgument -> {
            val channel = resolvedObjects?.channels.orEmpty()[value.value]
            val user = resolvedObjects?.users.orEmpty()[value.value]
            val member = resolvedObjects?.members.orEmpty()[value.value]
            val role = resolvedObjects?.roles.orEmpty()[value.value]

            val entity = channel ?: member ?: user ?: role
            requireNotNull(entity) { "user, member, or channel expected for $value but was missing" }

            OptionValue.MentionableOptionValue(entity, focused)
        }

        is CommandArgument.RoleArgument -> {
            val role = resolvedObjects?.roles.orEmpty()[value.value]
            requireNotNull(role) { "role expected for $value but was missing" }

            OptionValue.RoleOptionValue(role, focused)
        }

        is CommandArgument.UserArgument -> {
            val member = resolvedObjects?.members.orEmpty()[value.value]

            if (member != null) return OptionValue.MemberOptionValue(member, focused)

            val user = resolvedObjects?.users.orEmpty()[value.value]
            requireNotNull(user) { "user expected for $value but was missing" }

            OptionValue.UserOptionValue(user, focused)
        }
    }
}


public sealed interface GlobalInteraction : Interaction {
    override val user: User get() = User(data.user.value!!, kord)
}

/**
 * An [ActionInteraction] that took place in a Global Context with [GlobalApplicationCommand].
 */
public sealed interface GlobalApplicationCommandInteraction : ApplicationCommandInvocationInteraction, GlobalInteraction {
    /**
     * The user who invoked the interaction.
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalApplicationCommandInteraction =
        GlobalApplicationCommandInteraction(data, kord, strategy.supply(kord))

    override val applicationId: Snowflake
        get() = super<ApplicationCommandInvocationInteraction>.applicationId
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


public sealed interface GuildApplicationCommandInteraction : ApplicationCommandInvocationInteraction, GuildInteractionBehavior {

    override val guildId: Snowflake
        get() = data.guildId.value!!

    /**
     * Overridden permissions of the interaction invoker in the channel.
     */
    public val permissions: Permissions get() = data.permissions.value!!


    /**
     * The invoker of the command as [MemberBehavior].
     */
    public val member: Member
        get() = Member(
            data.member.value!!,
            data.user.value!!,
            kord
        )

    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    override val user: UserBehavior
        get() = UserBehavior(member.id, kord)

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


public fun OptionValue<*>.user(): User = value as User


public fun OptionValue<*>.channel(): ResolvedChannel = value as ResolvedChannel


public fun OptionValue<*>.role(): Role = value as Role


public fun OptionValue<*>.member(): Member = value as Member


public fun OptionValue<*>.string(): String = value.toString()


public fun OptionValue<*>.boolean(): Boolean = value as Boolean


public fun OptionValue<*>.int(): Long = value as Long


public fun OptionValue<*>.number(): Double = value as Double


public fun OptionValue<*>.mentionable(): Entity {
    return value as Entity
}
