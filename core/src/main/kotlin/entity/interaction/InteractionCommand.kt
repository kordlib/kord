package dev.kord.core.entity.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.ApplicationInteractionData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.ResolvedChannel

/**
 * The base interface of all commands that can be executed under a [ChatInputCommandInteraction].
 *
 * See [here](https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups)
 * for more information about sub-commands and groups.
 *
 * @see RootCommand
 * @see SubCommand
 * @see GroupCommand
 */
public sealed interface InteractionCommand : KordObject {
    public val data: ApplicationInteractionData

    /** The id of the root command. */
    public val rootId: Snowflake get() = data.id.value!!

    /** The name of the root command. */
    public val rootName: String get() = data.name.value!!

    /** The values passed to the command. */
    public val options: Map<String, OptionValue<*>>

    public val resolvedObjects: ResolvedObjects? get() = data.resolvedObjectsData.unwrap { ResolvedObjects(it, kord) }

    public val strings: Map<String, String> get() = filterOptions()

    public val integers: Map<String, Long> get() = filterOptions()

    public val numbers: Map<String, Double> get() = filterOptions()

    public val booleans: Map<String, Boolean> get() = filterOptions()

    public val users: Map<String, User> get() = filterOptions()

    public val members: Map<String, Member> get() = filterOptions()

    public val channels: Map<String, ResolvedChannel> get() = filterOptions()

    public val roles: Map<String, Role> get() = filterOptions()

    public val mentionables: Map<String, Entity> get() = filterOptions()

    public val attachments: Map<String, Attachment> get() = filterOptions()

    private inline fun <reified T> filterOptions(): Map<String, T> = buildMap {
        options.forEach { (key, value) ->
            when (value) {
                is ResolvableOptionValue<*> -> {
                    val resolvedObject = value.resolvedObject
                    if (resolvedObject is T) put(key, resolvedObject)
                }
                else -> {
                    val wrappedValue = value.value
                    if (wrappedValue is T) put(key, wrappedValue)
                }
            }
        }
    }
}


public fun InteractionCommand(
    data: ApplicationInteractionData,
    kord: Kord,
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
        else -> error("The interaction data provided is not a chat input command")
    }
}


/**
 * Represents an invocation of a root command.
 *
 * The root command is the first command defined in a slash-command structure.
 *
 * See [here](https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups)
 * for more information about sub-commands and groups.
 */
public class RootCommand(
    override val data: ApplicationInteractionData,
    override val kord: Kord,
) : InteractionCommand {

    override val options: Map<String, OptionValue<*>>
        get() = data.options.orEmpty()
            .associate { it.name to OptionValue(it.value.value!!, resolvedObjects) }
}


/**
 * Represents an invocation of a sub-command under the root command.
 *
 * See [here](https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups)
 * for more information about sub-commands and groups.
 */
public class SubCommand(
    override val data: ApplicationInteractionData,
    override val kord: Kord,
) : InteractionCommand {

    private val subCommandData = data.options.orEmpty().first()

    /** The name of the sub-command. */
    public val name: String get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.values.orEmpty()
            .associate { it.name to OptionValue(it, resolvedObjects) }
}


/**
 * Represents an invocation of a sub-command under a group under the root command.
 *
 * See [here](https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups)
 * for more information about sub-commands and groups.
 */
public class GroupCommand(
    override val data: ApplicationInteractionData,
    override val kord: Kord,
) : InteractionCommand {

    private val groupData get() = data.options.orEmpty().first()
    private val subCommandData get() = groupData.subCommands.orEmpty().first()

    /** The name of the group of this sub-command. */
    public val groupName: String get() = groupData.name

    /** The name of this sub-command. */
    public val name: String get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.options.orEmpty()
            .associate { it.name to OptionValue(it, resolvedObjects) }
}
