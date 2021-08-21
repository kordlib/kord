package dev.kord.core.entity.application

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.behavior.ApplicationCommandBehavior
import dev.kord.core.behavior.GlobalApplicationCommandBehavior
import dev.kord.core.behavior.GuildApplicationCommandBehavior
import dev.kord.core.cache.data.*
import dev.kord.rest.service.InteractionService

/**
 * A representation of a [Discord Application Command](https://discord.com/developers/docs/interactions/application-commands)
 */
sealed interface ApplicationCommand : ApplicationCommandBehavior {

    val data: ApplicationCommandData

    override val id: Snowflake
        get() = data.id

     val type: ApplicationCommandType
        get() = data.type.value!!

    override val applicationId: Snowflake
        get() = data.applicationId

    val name: String
        get() = data.name


}


interface GlobalApplicationCommand : ApplicationCommand, GlobalApplicationCommandBehavior
class UnknownGlobalApplicationCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : GlobalApplicationCommand


/**
 * A representation of [Discord Application Command](https://discord.com/developers/docs/interactions/application-commands)
 * in a global context.
 */
fun GlobalApplicationCommand(data: ApplicationCommandData, service: InteractionService): GlobalApplicationCommand {
    return when(data.type.value) {
        ApplicationCommandType.ChatInput -> GlobalChatInputCommand(data, service)
        ApplicationCommandType.Message -> GlobalMessageCommand(data, service)
        ApplicationCommandType.User -> GlobalUserCommand(data, service)
        is ApplicationCommandType.Unknown ->  UnknownGlobalApplicationCommand(data, service)
        null -> error("The type value is missing, can't determine the type")
    }
}


/**
 * A representation of [Discord Application Command](https://discord.com/developers/docs/interactions/application-commands)
 * in a guild context
 */
sealed interface GuildApplicationCommand : ApplicationCommand, GuildApplicationCommandBehavior

class UnknownGuildApplicationCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : GuildApplicationCommand {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}




fun GuildApplicationCommand(data: ApplicationCommandData, service: InteractionService): GuildApplicationCommand {
    return when(data.type.value) {
        ApplicationCommandType.ChatInput -> GuildChatInputCommand(data, service)
        ApplicationCommandType.Message -> GuildMessageCommand(data, service)
        ApplicationCommandType.User -> GuildUserCommand(data, service)
        is ApplicationCommandType.Unknown ->  UnknownGuildApplicationCommand(data, service)
        null -> error("The type value is missing, can't determine the type")
    }
}



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
