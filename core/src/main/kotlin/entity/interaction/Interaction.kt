package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordOptionValue
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.*
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.ResolvedObjectsData
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.toSnowflakeOrNull

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

    /**
     * The [MessageChannelBehavior] of the channel the command was executed in.
     */
    open val channel: MessageChannelBehavior get() = MessageChannelBehavior(data.channelId, kord)

    abstract val user: UserBehavior

    /**
     * [InteractionCommand] object that contains the data related to the interaction's command.
     */
    val command: InteractionCommand
        get() = InteractionCommand(data.data, kord)

    /**
     * read-only property, always 1
     */
    val version: Int get() = data.version

    companion object {
        fun from(
            data: InteractionData,
            kord: Kord,
            strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): Interaction {
            return if (data.guildId !is OptionalSnowflake.Missing)
                GuildInteraction(data, data.applicationId, kord, strategy.supply(kord))
            else
                DmInteraction(data, data.applicationId, kord, strategy.supply(kord))
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
    data: ApplicationCommandInteractionData,
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
    val data: ApplicationCommandInteractionData,
    override val kord: Kord
) : InteractionCommand() {

    override val rootId: Snowflake
        get() = data.id

    override val rootName get() = data.name

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
    val data: ApplicationCommandInteractionData,
    override val kord: Kord
) : InteractionCommand() {

    private val subCommandData = data.options.orEmpty().first()

    override val rootName get() = data.name

    override val rootId: Snowflake
        get() = data.id

    /**
     * Name of the sub-command executed.
     */
    val name get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.values.orEmpty()
            .associate { it.name to OptionValue(it.value, resolved) }


    override val resolved: ResolvedObjects?
        get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }


}

/**
 * Represents an invocation of a sub-command under a group.
 */
@KordPreview
class GroupCommand(
    val data: ApplicationCommandInteractionData,
    override val kord: Kord
) : InteractionCommand() {

    private val groupData get() = data.options.orEmpty().first()
    private val subCommandData get() = groupData.subCommands.orEmpty().first()

    override val rootId: Snowflake
        get() = data.id

    override val rootName get() = data.name

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
            .associate { it.name to OptionValue(it.value, resolved) }


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
            Member(
                it.value,
                users!!.get(it.key)!!.data,
                kord
            )
        }.value

}

@KordPreview
sealed class OptionValue<T>(val value: T) {

    class RoleOptionValue(value: Role) : OptionValue<Role>(value)
    open class UserOptionValue(value: User) : OptionValue<User>(value)
    class MemberOptionValue(value: Member) : UserOptionValue(value)
    class ChannelOptionValue(value: ResolvedChannel) : OptionValue<ResolvedChannel>(value)
    class IntOptionValue(value: Int) : OptionValue<Int>(value)
    class StringOptionValue(value: String) : OptionValue<String>(value)
    class BooleanOptionValue(value: Boolean) : OptionValue<Boolean>(value)
}

@KordPreview
fun OptionValue(value: DiscordOptionValue<*>, resolvedObjects: ResolvedObjects?): OptionValue<*> {
    return when (value) {
        is DiscordOptionValue.BooleanValue -> OptionValue.BooleanOptionValue(value.value)
        is DiscordOptionValue.IntValue -> OptionValue.IntOptionValue(value.value)
        is DiscordOptionValue.StringValue -> {
            if (resolvedObjects == null) return OptionValue.StringOptionValue(value.value)

            val string = value.value
            val snowflake = string.toLongOrNull().toSnowflakeOrNull() ?: return OptionValue.StringOptionValue(string)

            when {
                resolvedObjects.members?.get(snowflake) != null ->
                    OptionValue.MemberOptionValue(resolvedObjects.members?.get(snowflake)!!)
                resolvedObjects.users?.get(snowflake) != null ->
                    OptionValue.UserOptionValue(resolvedObjects.users?.get(snowflake)!!)
                resolvedObjects.channels?.get(snowflake) != null ->
                    OptionValue.ChannelOptionValue(resolvedObjects.channels?.get(snowflake)!!)
                resolvedObjects.roles?.get(snowflake) != null ->
                    OptionValue.RoleOptionValue(resolvedObjects.roles?.get(snowflake)!!)
                else -> OptionValue.StringOptionValue(string)
            }
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
    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.value!!.userId, kord)

    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    override val user: UserBehavior
        get() = UserBehavior(member.id, kord)


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
