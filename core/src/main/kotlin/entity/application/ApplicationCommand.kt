package dev.kord.core.entity.application

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
public sealed interface ApplicationCommand : ApplicationCommandBehavior {

    public val data: ApplicationCommandData

    override val id: Snowflake
        get() = data.id

     public val type: ApplicationCommandType
        get() = data.type.value!!

    override val applicationId: Snowflake
        get() = data.applicationId

    public val name: String
        get() = data.name

    /**
     * auto-incrementing version identifier updated during substantial record changes.
     */
    public val version: Snowflake get() = data.version

    /**
     * whether the command is enabled by default when the app is added to a guild.
     */
    public val defaultPermission: Boolean? get() = data.defaultPermission.discordBoolean


}


public interface GlobalApplicationCommand : ApplicationCommand, GlobalApplicationCommandBehavior
public class UnknownGlobalApplicationCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : GlobalApplicationCommand


/**
 * A representation of [Discord Application Command](https://discord.com/developers/docs/interactions/application-commands)
 * in a global context.
 */
public fun GlobalApplicationCommand(data: ApplicationCommandData, service: InteractionService): GlobalApplicationCommand {
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
public sealed interface GuildApplicationCommand : ApplicationCommand, GuildApplicationCommandBehavior

public class UnknownGuildApplicationCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService,
) : GuildApplicationCommand {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}




public fun GuildApplicationCommand(data: ApplicationCommandData, service: InteractionService): GuildApplicationCommand {
    return when(data.type.value) {
        ApplicationCommandType.ChatInput -> GuildChatInputCommand(data, service)
        ApplicationCommandType.Message -> GuildMessageCommand(data, service)
        ApplicationCommandType.User -> GuildUserCommand(data, service)
        is ApplicationCommandType.Unknown ->  UnknownGuildApplicationCommand(data, service)
        null -> error("The type value is missing, can't determine the type")
    }
}



public class ApplicationCommandParameter(
    public val data: ApplicationCommandParameterData
) {

    /**
     * The name of the parameter.
     */
    public val name: String get() = data.name

    /**
     * The description of the parameter.
     */
    public val description: String get() = data.description

    /**
     * Whether this parameter requires a value when invoking the command.
     */
    public val isRequired: Boolean get() = data.required.discordBoolean

    /**
     * The accepted choices of the parameter. Is empty if the parameter does not have any choices.
     */
    public val choices: Map<String, String> get() = data.choices.orEmpty().associate { it.name to it.value }
}
