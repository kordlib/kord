package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.filterList
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.behavior.ApplicationCommandBehavior
import dev.kord.core.behavior.GlobalApplicationCommandBehavior
import dev.kord.core.behavior.GuildApplicationCommandBehavior
import dev.kord.core.cache.data.*
import dev.kord.rest.service.InteractionService

@KordPreview
abstract class ApplicationCommand(
    val data: ApplicationCommandData,
    override val service: InteractionService
) : ApplicationCommandBehavior {

    override val id: Snowflake
        get() = data.id

    /**
     * The id of the targeted application for this command
     */
    override val applicationId: Snowflake
        get() = data.applicationId

    val name: String
        get() = data.name

    val description: String
        get() = data.description

    /**
     * [Snowflake] of last time the command was edited
     */

    val version: Snowflake get() = data.version

    /**
     * The groups of this command, each group contains at least one [sub command][ApplicationCommandSubCommand].
     */
    val groups: Map<String, ApplicationCommandGroup>
        get() = data.options.filterList { it.type == ApplicationCommandOptionType.SubCommandGroup }
            .orEmpty().associate { it.name to ApplicationCommandGroup(ApplicationCommandGroupData(it)) }

    /**
     * The directly nested sub commands of this command.
     */
    val subCommands: Map<String, ApplicationCommandSubCommand>
        get() = data.options.filterList { it.type == ApplicationCommandOptionType.SubCommand }
            .orEmpty().associate { it.name to ApplicationCommandSubCommand(ApplicationCommandSubCommandData(it)) }
}

@KordPreview
class GlobalApplicationCommand(
    data: ApplicationCommandData,
    service: InteractionService,
) : ApplicationCommand(data, service), GlobalApplicationCommandBehavior

@KordPreview
class GuildApplicationCommand(
    data: ApplicationCommandData,
    service: InteractionService,
    override val guildId: Snowflake,
) : ApplicationCommand(data, service), GuildApplicationCommandBehavior

@KordPreview
class ApplicationCommandGroup(
    val data: ApplicationCommandGroupData
) {
    /**
     * The name of the group.
     */
    val name: String get() = data.name

    /**
     * The description of the group.
     */
    val description: String get() = data.description

    /**
     * The commands directly nested in this group, this map has at least one value.
     */
    val subcommands: Map<String, ApplicationCommandSubCommand>
        get() = data.subCommands.associate { it.name to ApplicationCommandSubCommand(it) }
}


@KordPreview
class ApplicationCommandSubCommand(
    val data: ApplicationCommandSubcommandData
) {

    /**
     * The name of the sub command.
     */
    val name: String get() = data.name

    /**
     * The description of the sub command.
     */
    val description: String get() = data.description

    /**
     * The parameters of this sub command. Is empty if the command takes no parameters.
     */
    val parameters: Map<String, ApplicationCommandParameter>
        get() = data.parameters.orEmpty().associate { it.name to ApplicationCommandParameter(it) }
}


@KordPreview
class ApplicationCommandParameter(
    val data: ApplicationCommandParameterData
) {

    /**
     * The name of the parameter.
     */
    val name: String get() = data.name

    /**
     * The description of the parameter.
     */
    val description: String get() = data.description

    /**
     * Whether this parameter requires a value when invoking the command.
     */
    val isRequired: Boolean get() = data.required.discordBoolean

    /**
     * The accepted choices of the parameter. Is empty if the parameter does not have any choices.
     */
    val choices: Map<String, String> get() = data.choices.orEmpty().associate { it.name to it.value }
}
