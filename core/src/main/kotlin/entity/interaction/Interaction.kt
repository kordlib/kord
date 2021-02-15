package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.OptionValue
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.InteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.supplier.EntitySupplier

/**
 * Interaction that can respond to interactions and follow them up.
 */
@KordPreview
class Interaction(
    val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : InteractionBehavior {

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
     * The id of the guild where the interaction took place.
     */
    override val guildId: Snowflake get() = data.guildId

    /**
     * The type of the interaction.
     */
    val type: InteractionType get() = data.type

    /**
     * The permissions of the member with the overwrites.
     */
    val permissions: Permissions get() = data.permissions

    /**
     * The [TextChannelBehavior] of the channel the command was executed in.
     */
    val channel: TextChannelBehavior get() = TextChannelBehavior(id = channelId, guildId = guildId, kord = kord)

    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    val guild get() = GuildBehavior(guildId, kord)

    /**
     * The invoker of the command as [MemberBehavior].
     */
    val member: MemberBehavior get() = MemberBehavior(data.guildId, data.member.userId, kord)

    /**
     * [InteractionCommand] object that contains the data related to the interaction's command.
     */
    val command: InteractionCommand
        get() = InteractionCommand(data.data)

    /**
     * read-only property, always 1
     */
    val version: Int get() = data.version
}

/**
 * The base command of all commands that can be executed under an interaction event.
 */
@KordPreview
sealed class InteractionCommand {
    /**
     * The id of the root command.
     */
    abstract val rootId: Snowflake

    /**
     * The root command name
     */
    abstract val rootName: String

    /**
     * Options passed to the command.
     * Type-check your command to against the sub-types of this class for the right context of the execution
     * * [RootCommand] - Context of main command execution.
     * * [GroupCommand] - Context of a sub-command executed in a group.
     * * [SubCommand] - Context of a sub-command executed under the root command.
     */
    abstract val options: Map<String, OptionValue<*>>

    companion object {
        operator fun invoke(data: ApplicationCommandInteractionData): InteractionCommand {
            val firstLevelOptions = data.options.orEmpty()
            val rootPredicate = firstLevelOptions.isEmpty() || firstLevelOptions.any { it.value.value != null }
            val groupPredicate = firstLevelOptions.any { it.subCommands.orEmpty().isNotEmpty() }

            return when {
                rootPredicate -> RootCommand(data)
                groupPredicate -> GroupCommand(data)
                else -> SubCommand(data) // if not root, or group, it's a sub-command
            }
        }
    }
}

/**
 * Represents an invocation of a root command.
 *
 * The root command is the first command defined in in a slash-command structure.
 */
@KordPreview
class RootCommand(val data: ApplicationCommandInteractionData) : InteractionCommand() {

    override val rootId: Snowflake
        get() = data.id

    override val rootName get() = data.name

    override val options: Map<String, OptionValue<*>>
        get() = data.options.orEmpty()
            .associate { it.name to it.value.value!! }

}

/**
 * Represents an invocation of a sub-command under the [RootCommand]
 */
@KordPreview
class SubCommand(val data: ApplicationCommandInteractionData) : InteractionCommand() {

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
            .associate { it.name to it.value }

}

/**
 * Represents an invocation of a sub-command under a group.
 */
@KordPreview
class GroupCommand(val data: ApplicationCommandInteractionData) : InteractionCommand() {

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
            .associate { it.name to it.value }
}



