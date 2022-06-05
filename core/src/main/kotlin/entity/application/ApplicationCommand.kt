package dev.kord.core.entity.application

import dev.kord.common.Locale
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.value
import dev.kord.core.behavior.ApplicationCommandBehavior
import dev.kord.core.behavior.GlobalApplicationCommandBehavior
import dev.kord.core.behavior.GuildApplicationCommandBehavior
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.ApplicationCommandParameterData
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

    /**
     * The name of the command
     */
    public val name: String
        get() = data.name

    /**
     * A map containing all localizations of [name].
     */
    public val nameLocalizations: Map<Locale, String>
        get() = data.nameLocalizations.value ?: emptyMap()

    /**
     * auto-incrementing version identifier updated during substantial record changes.
     */
    public val version: Snowflake get() = data.version

    /**
     * Set of [Permissions] required to execute this command unless a server admin changed them.
     */
    public val defaultMemberPermissions: Permissions? get() = data.defaultMemberPermissions.value

    /**
     * whether the command is enabled by default when the app is added to a guild.
     */
    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'.")
    public val defaultPermission: Boolean? get() = data.defaultPermission.value


}


public interface GlobalApplicationCommand : ApplicationCommand, GlobalApplicationCommandBehavior {
    /**
     * Whether this command is available in DMs with the application.
     */
    public val dmPermission: Boolean get() = data.dmPermission?.value ?: true
}
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

    public val channelTypes: List<ChannelType> get() = data.channelTypes.orEmpty()
}
