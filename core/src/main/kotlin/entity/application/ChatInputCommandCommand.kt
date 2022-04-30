package dev.kord.core.entity.application

import dev.kord.common.Locale
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.filterList
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.behavior.ChatInputCommandBehavior
import dev.kord.core.behavior.GlobalChatInputCommandBehavior
import dev.kord.core.behavior.GuildChatInputCommandBehavior
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.ApplicationCommandGroupData
import dev.kord.core.cache.data.ApplicationCommandSubCommandData
import dev.kord.core.cache.data.ApplicationCommandSubcommandData
import dev.kord.rest.service.InteractionService

public sealed interface ChatInputCommandCommand : ApplicationCommand, ChatInputCommandBehavior {

    /**
     * The description of the command.
     */
    public val description: String?
        get() = data.description

    /**
     * A map containing all localizations of [description].
     */
    public val descriptionLocalizations: Map<Locale, String>
        get() = data.descriptionLocalizations.value ?: emptyMap()

    /**
     * The groups of this command, each group contains at least one [sub command][ChatInputSubCommand].
     */
    public val groups: Map<String, ChatInputGroup>
        get() = data.options.filterList { it.type == ApplicationCommandOptionType.SubCommandGroup }
            .orEmpty().associate { it.name to ChatInputGroup(ApplicationCommandGroupData(it)) }

    /**
     * The directly nested sub commands of this command.
     */
    public val subCommands: Map<String, ChatInputSubCommand>
        get() = data.options.filterList { it.type == ApplicationCommandOptionType.SubCommand }
            .orEmpty().associate { it.name to ChatInputSubCommand(ApplicationCommandSubCommandData(it)) }

}

public class ChatInputGroup(
    public val data: ApplicationCommandGroupData
) {
    /**
     * The name of the group.
     */
    public val name: String get() = data.name

    /**
     * The description of the group.
     */
    public val description: String get() = data.description

    /**
     * The commands directly nested in this group, this map has at least one value.
     */
    public val subcommands: Map<String, ChatInputSubCommand>
        get() = data.subCommands.associate { it.name to ChatInputSubCommand(it) }
}

public class ChatInputSubCommand(
    public val data: ApplicationCommandSubcommandData
) {

    /**
     * The name of the sub command.
     */
    public val name: String get() = data.name

    /**
     * The description of the sub command.
     */
    public val description: String get() = data.description


    /**
     * The parameters of this sub command. Is empty if the command takes no parameters.
     */
    public val parameters: Map<String, ApplicationCommandParameter>
        get() = data.parameters.orEmpty().associate { it.name to ApplicationCommandParameter(it) }
}

public class GlobalChatInputCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : ChatInputCommandCommand, GlobalApplicationCommand,  GlobalChatInputCommandBehavior

public class GuildChatInputCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : ChatInputCommandCommand, GuildApplicationCommand, GuildChatInputCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}
