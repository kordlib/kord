package dev.kord.core.entity.application

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.filterList
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.behavior.ChatInputCommandBehavior
import dev.kord.core.behavior.GlobalInputCommandBehavior
import dev.kord.core.behavior.GuildInputCommandBehavior
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.ApplicationCommandGroupData
import dev.kord.core.cache.data.ApplicationCommandSubCommandData
import dev.kord.core.cache.data.ApplicationCommandSubcommandData
import dev.kord.rest.service.InteractionService



 sealed interface ChatInputCommandCommand : ApplicationCommand, ChatInputCommandBehavior {

    val description: String
        get() = data.description
    /**
     * The groups of this command, each group contains at least one [sub command][ChatInputSubCommand].
     */
    val groups: Map<String, ChatInputGroup>
        get() = data.options.filterList { it.type == ApplicationCommandOptionType.SubCommandGroup }
            .orEmpty().associate { it.name to ChatInputGroup(ApplicationCommandGroupData(it)) }

    /**
     * The directly nested sub commands of this command.
     */
    val subCommands: Map<String, ChatInputSubCommand>
        get() = data.options.filterList { it.type == ApplicationCommandOptionType.SubCommand }
            .orEmpty().associate { it.name to ChatInputSubCommand(ApplicationCommandSubCommandData(it)) }

}


class ChatInputGroup(
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
    val subcommands: Map<String, ChatInputSubCommand>
        get() = data.subCommands.associate { it.name to ChatInputSubCommand(it) }
}



class ChatInputSubCommand(
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



class GlobalChatInputCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : ChatInputCommandCommand, GlobalApplicationCommand,  GlobalInputCommandBehavior



class GuildChatInputCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : ChatInputCommandCommand, GuildApplicationCommand, GuildInputCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}

