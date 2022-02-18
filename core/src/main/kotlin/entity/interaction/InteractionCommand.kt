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
 */

public class RootCommand(
    public val data: ApplicationInteractionData,
    override val kord: Kord,
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
    override val kord: Kord,
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
    override val kord: Kord,
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
