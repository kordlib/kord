package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.GuildInteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.cache.data.ApplicationInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.ResolvedObjectsData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of [Interaction] (https://discord.com/developers/docs/interactions/slash-commands#interaction)
 */
@KordPreview
sealed class Interaction : InteractionBehavior {

    abstract val data: InteractionData

    override val id: Snowflake get() = data.id

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

    abstract val user: UserBehavior

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
                data.type == InteractionType.Component -> ComponentInteraction(
                    data,
                    data.applicationId,
                    kord,
                    strategy.supply(kord)
                )
                data.guildId !is OptionalSnowflake.Missing -> GuildInteraction(
                    data,
                    data.applicationId,
                    kord,
                    strategy.supply(kord)
                )
                else -> DmInteraction(data, data.applicationId, kord, strategy.supply(kord))
            }
        }
    }

}

/**
 * The base command of all commands that can be executed under an interaction event.
 */
@KordPreview
sealed class InteractionCommand : KordObject {
    /**
     * The id of the root command.
     */
    abstract val rootId: Snowflake

    /**
     * The root command name
     */
    abstract val rootName: String

    /**
     * the values passed to the command.
     */
    abstract val options: Map<String, OptionValue<*>>

    abstract val resolved: ResolvedObjects?
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
 * The root command is the first command defined in in a slash-command structure.
 */
@KordPreview
class RootCommand(
    val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand() {

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
@KordPreview
class SubCommand(
    val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand() {

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
@KordPreview
class GroupCommand(
    val data: ApplicationInteractionData,
    override val kord: Kord
) : InteractionCommand() {

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

@KordPreview
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
        get() = data.members.mapValues {
            Member(it.value, users!![it.key]!!.data, kord)
        }.value

}

@KordPreview
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

    class StringOptionValue(value: String) : OptionValue<String>(value) {
        override fun toString(): String = "StringOptionValue(value=$value)"
    }

    class BooleanOptionValue(value: Boolean) : OptionValue<Boolean>(value) {
        override fun toString(): String = "BooleanOptionValue(value=$value)"
    }

    class MentionableOptionValue(value: Entity) : OptionValue<Entity>(value) {
        override fun toString(): String = "MentionableOptionValue(value=$value)"
    }
}

@KordPreview
fun OptionValue(value: CommandArgument<*>, resolvedObjects: ResolvedObjects?): OptionValue<*> {
    return when (value) {
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
            val user = resolvedObjects?.channels.orEmpty()[value.value]
            val member = resolvedObjects?.members.orEmpty()[value.value]
            val role = resolvedObjects?.members.orEmpty()[value.value]

            OptionValue.MentionableOptionValue((channel ?: user ?: member ?: role)!!)
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
@KordPreview
class DmInteraction(
    override val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Interaction() {
    /**
     * The user who invoked the interaction.
     */
    override val user get() = User(data.user.value!!, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DmInteraction =
        DmInteraction(data, applicationId, kord, strategy.supply(kord))
}

@KordPreview
class ComponentInteraction(
    override val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : Interaction(), InteractionBehavior {
    override val user: UserBehavior = UserBehavior(data.member.value!!.userId, kord)

    val message: Message
        get() = data.message.unwrap {
            Message(it, kord, supplier)
        }!!

    val components: List<DiscordComponent> get() = message.components

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction = ComponentInteraction(
        data, applicationId, kord, strategy.supply(kord)
    )
}

@KordPreview
class GuildInteraction(
    override val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : Interaction(), GuildInteractionBehavior {

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
    val member: Member get() = Member(
        data.member.value!!,
        data.user.value!!,
        kord
    )

    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    override val user: UserBehavior
        get() = UserBehavior(member.id, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildInteraction =
        GuildInteraction(data, applicationId, kord, supplier)

}

@KordPreview
fun OptionValue<*>.user(): User = value as User

@KordPreview
fun OptionValue<*>.channel(): ResolvedChannel = value as ResolvedChannel

@KordPreview
fun OptionValue<*>.role(): Role = value as Role

@KordPreview
fun OptionValue<*>.member(): Member = value as Member

@KordPreview
fun OptionValue<*>.string() = value.toString()

@KordPreview
fun OptionValue<*>.boolean() = value as Boolean

@KordPreview
fun OptionValue<*>.int() = value as Int
