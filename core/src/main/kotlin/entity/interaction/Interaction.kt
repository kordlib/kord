package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.*
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.cache.data.ApplicationInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.ResolvedObjectsData
import dev.kord.core.entity.*
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.service.InteractionService

/**
 * An instance of [Interaction] (https://discord.com/developers/docs/interactions/slash-commands#interaction)
 */

sealed interface Interaction : InteractionBehavior {

    abstract val data: InteractionData

    override val id: Snowflake get() = data.id

    override val applicationId: Snowflake
        get() = data.applicationId

    /**
     * The channel id where the interaction took place.
     */
    override val channelId: Snowflake get() = data.channelId

    /**
     * a continuation token for responding to the interaction
     */
    override val token: String get() = data.token

    /**
     * The type of the interaction.
     */
    val type: InteractionType get() = data.type

    val user: UserBehavior

    /**
     * read-only property, always 1
     */
    val version: Int get() = data.version

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction

    companion object {
        fun from(
            data: InteractionData,
            kord: Kord,
            strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): Interaction {
            return when {
                data.type == InteractionType.Component -> ComponentInteraction(data, kord, strategy.supply(kord))
                data.guildId !is OptionalSnowflake.Missing -> GuildApplicationCommandInteraction(
                    data,
                    kord,
                    strategy.supply(kord)
                )
                else -> GlobalApplicationCommandInteraction(data, kord, strategy.supply(kord))
            }
        }
    }

}

/**
 * The base interaction for all slash-command related interactions.
 *
 * @see DmInteraction
 * @see GuildApplicationCommandInteraction
 */

sealed class CommandInteraction : Interaction {
    val command: InteractionCommand
        get() = InteractionCommand(data.data, kord)
}

/**
 * The base command of all commands that can be executed under an interaction event.
 */

sealed interface InteractionCommand : KordObject {
    /**
     * The id of the root command.
     */
    val rootId: Snowflake

    /**
     * The root command name
     */
    abstract val rootName: String

    /**
     * the values passed to the command.
     */
    val options: Map<String, OptionValue<*>>

    val resolved: ResolvedObjects?

    val strings: Map<String, String> get() = filterOptions(options)

    val integers: Map<String, Int> get() = filterOptions(options)

    val numbers: Map<String, Double> get() = filterOptions(options)

    val booleans: Map<String, Boolean> get() = filterOptions(options)

    private inline fun <reified T> filterOptions(options: Map<String, OptionValue<*>>): Map<String, T> {
        return buildMap {
            options.onEach { (key, value)  ->
                val wrappedValue = value.value
                if(wrappedValue is T) put(key, wrappedValue)
            }
        }
    }
}

fun InteractionCommand(
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
        else -> error("The interaction data provided is not an application command")
    }
}

/**
 * Represents an invocation of a root command.
 *
 * The root command is the first command defined in a slash-command structure.
 */

class RootCommand(
    val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand {

    override val rootId: Snowflake
        get() = data.id.value!!

    override val rootName get() = data.name.value!!

    override val options: Map<String, OptionValue<*>>
        get() = data.options.orEmpty()
            .associate { it.name to OptionValue(it.value.value!!, resolved) }

    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

}

/**
 * Represents an invocation of a sub-command under the [RootCommand]
 */

class SubCommand(
    val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand {

    private val subCommandData = data.options.orEmpty().first()

    override val rootName get() = data.name.value!!

    override val rootId: Snowflake
        get() = data.id.value!!

    /**
     * Name of the sub-command executed.
     */
    val name get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.values.orEmpty()
            .associate { it.name to OptionValue(it, resolved) }


    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }


}

/**
 * Represents an invocation of a sub-command under a group.
 */

class GroupCommand(
    val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand {

    private val groupData get() = data.options.orEmpty().first()
    private val subCommandData get() = groupData.subCommands.orEmpty().first()

    override val rootId: Snowflake
        get() = data.id.value!!

    override val rootName get() = data.name.value!!

    /**
     * Name of the group of this sub-command.
     */
    val groupName get() = groupData.name

    /**
     * Name of this sub-command
     */
    val name get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.options.orEmpty()
            .associate { it.name to OptionValue(it, resolved) }


    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

}


class ResolvedObjects(
    val data: ResolvedObjectsData,
    val kord: Kord,
    val strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) {
    val channels: Map<Snowflake, ResolvedChannel>?
        get() = data.channels.mapValues { ResolvedChannel(it.value, kord, strategy) }.value

    val roles: Map<Snowflake, Role>? get() = data.roles.mapValues { Role(it.value, kord) }.value

    val users: Map<Snowflake, User>? get() = data.users.mapValues { User(it.value, kord) }.value

    val members: Map<Snowflake, Member>?
        get() = data.members.mapValues { Member(it.value, users!![it.key]!!.data, kord) }.value

    val messages: Map<Snowflake, Message>?
        get() = data.messages.mapValues { Message(it.value, kord) }.value

}


sealed class OptionValue<out T>(val value: T) {

    class RoleOptionValue(value: Role) : OptionValue<Role>(value) {
        override fun toString(): String = "RoleOptionValue(value=$value)"
    }

    open class UserOptionValue(value: User) : OptionValue<User>(value) {
        override fun toString(): String = "UserOptionValue(value=$value)"
    }

    class MemberOptionValue(value: Member) : UserOptionValue(value) {
        override fun toString(): String = "MemberOptionValue(value=$value)"
    }

    class ChannelOptionValue(value: ResolvedChannel) : OptionValue<ResolvedChannel>(value) {
        override fun toString(): String = "ChannelOptionValue(value=$value)"
    }

    class IntOptionValue(value: Int) : OptionValue<Int>(value) {
        override fun toString(): String = "IntOptionValue(value=$value)"
    }


    class NumberOptionValue(value: Double) : OptionValue<Double>(value) {
        override fun toString(): String = "DoubleOptionValue(value=$value)"
    }

    class StringOptionValue(value: String) : OptionValue<String>(value) {
        override fun toString(): String = "StringOptionValue(value=$value)"
    }

    class BooleanOptionValue(value: Boolean) : OptionValue<Boolean>(value) {
        override fun toString(): String = "BooleanOptionValue(value=$value)"
    }

    class MentionableOptionValue(value: Entity) : OptionValue<Entity>(value) {
        override fun toString(): String = "MentionableOptionValue(value=$value)"
    }

    class MessageOptionValue(value: Message) : OptionValue<Message>(value) {
        override fun toString(): String = "MessageOptionValue(value=$value)"
    }
}


fun OptionValue(value: CommandArgument<*>, resolvedObjects: ResolvedObjects?): OptionValue<*> {
    return when (value) {
        is CommandArgument.NumberArgument -> OptionValue.NumberOptionValue(value.value)
        is CommandArgument.BooleanArgument -> OptionValue.BooleanOptionValue(value.value)
        is CommandArgument.IntegerArgument -> OptionValue.IntOptionValue(value.value)
        is CommandArgument.StringArgument -> OptionValue.StringOptionValue(value.value)
        is CommandArgument.ChannelArgument -> {
            val channel = resolvedObjects?.channels.orEmpty()[value.value]
            requireNotNull(channel) { "channel expected for $value but was missing" }

            OptionValue.ChannelOptionValue(channel)
        }

        is CommandArgument.MentionableArgument -> {
            val channel = resolvedObjects?.channels.orEmpty()[value.value]
            val user = resolvedObjects?.users.orEmpty()[value.value]
            val member = resolvedObjects?.members.orEmpty()[value.value]
            val role = resolvedObjects?.roles.orEmpty()[value.value]

            val entity = channel ?: member ?: user ?: role
            requireNotNull(entity) { "user, member, or channel expected for $value but was missing" }

            OptionValue.MentionableOptionValue(entity)
        }

        is CommandArgument.RoleArgument -> {
            val role = resolvedObjects?.roles.orEmpty()[value.value]
            requireNotNull(role) { "role expected for $value but was missing" }

            OptionValue.RoleOptionValue(role)
        }

        is CommandArgument.UserArgument -> {
            val member = resolvedObjects?.members.orEmpty()[value.value]

            if (member != null) return OptionValue.MemberOptionValue(member)

            val user = resolvedObjects?.users.orEmpty()[value.value]
            requireNotNull(user) { "user expected for $value but was missing" }

            OptionValue.UserOptionValue(user)
        }
    }
}


/**
 * An [Interaction] that took place in a [DmChannel].
 */

sealed interface GlobalApplicationCommandInteraction : ApplicationCommandInteraction, GlobalApplicationCommandBehavior {
    /**
     * The user who invoked the interaction.
     */

    override val service: InteractionService
        get() = kord.rest.interaction

    override val user get() = User(data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GlobalApplicationCommandInteraction =
        GlobalApplicationCommandInteraction(data, kord, strategy.supply(kord))

    override val applicationId: Snowflake
        get() = super.applicationId
}

fun GlobalApplicationCommandInteraction(
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


sealed interface GuildApplicationCommandInteraction : ApplicationCommandInteraction, GuildInteractionBehavior {

    override val guildId: Snowflake
        get() = data.guildId.value!!

    /**
     * Overridden permissions of the interaction invoker in the channel.
     */
    val permissions: Permissions get() = data.permissions.value!!

    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    val guild get() = GuildBehavior(guildId, kord)

    /**
     * The invoker of the command as [MemberBehavior].
     */
    val member: Member
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

fun GuildApplicationCommandInteraction(
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


fun OptionValue<*>.user(): User = value as User


fun OptionValue<*>.channel(): ResolvedChannel = value as ResolvedChannel


fun OptionValue<*>.role(): Role = value as Role


fun OptionValue<*>.member(): Member = value as Member


fun OptionValue<*>.string() = value.toString()


fun OptionValue<*>.boolean() = value as Boolean


fun OptionValue<*>.int() = value as Int


fun OptionValue<*>.number() = value as Double


fun OptionValue<*>.mentionable(): Entity {
    return value as Entity
}
