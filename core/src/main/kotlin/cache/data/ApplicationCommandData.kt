package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.Locale
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
public data class ApplicationCommandData(
    val id: Snowflake,
    val type: Optional<ApplicationCommandType> = Optional.Missing(),
    val applicationId: Snowflake,
    val name: String,
    val nameLocalizations: Optional<Map<Locale, String>?> = Optional.Missing(),
    val description: String?,
    val descriptionLocalizations: Optional<Map<Locale, String>?> = Optional.Missing(),
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val options: Optional<List<ApplicationCommandOptionData>> = Optional.Missing(),
    val defaultMemberPermissions: Optional<Permissions?> = Optional.Missing(),
    val dmPermission: OptionalBoolean? = OptionalBoolean.Missing,
    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'.")
    val defaultPermission: OptionalBoolean? = OptionalBoolean.Missing,
    val version: Snowflake
) {
    public companion object {
        public val description: DataDescription<ApplicationCommandData, Snowflake> = description(ApplicationCommandData::id) {
            link(ApplicationCommandData::guildId to GuildData::id)
        }
        public fun from(command: DiscordApplicationCommand): ApplicationCommandData {
            return with(command) {
                ApplicationCommandData(
                    id,
                    type,
                    applicationId,
                    name,
                    nameLocalizations,
                    description,
                    descriptionLocalizations,
                    guildId,
                    options.mapList { ApplicationCommandOptionData.from(it) },
                    defaultMemberPermissions,
                    dmPermission,
                    defaultPermission,
                    version
                )
            }
        }
    }
}

@Serializable
public data class ApplicationCommandOptionData(
    val type: ApplicationCommandOptionType,
    val name: String,
    val description: String,
    val default: OptionalBoolean = OptionalBoolean.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    val choices: Optional<List<ApplicationCommandOptionChoiceData>> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOptionData>> = Optional.Missing(),
    val channelTypes: Optional<List<ChannelType>> = Optional.Missing()
) {
    public companion object {
        public fun from(data: ApplicationCommandOption): ApplicationCommandOptionData {
            return with(data) {
                ApplicationCommandOptionData(
                    type,
                    name,
                    description,
                    default,
                    required,
                    choices.mapList { ApplicationCommandOptionChoiceData.from(it) },
                    options.mapList { inner -> from(inner) },
                    channelTypes
                )
            }
        }
    }
}


@Serializable
public class ApplicationCommandGroupData(
    public val name: String,
    public val description: String,
    public val subCommands: List<ApplicationCommandSubcommandData>
)

@Suppress("FunctionName")
public fun ApplicationCommandGroupData(data: ApplicationCommandOptionData): ApplicationCommandGroupData {
    return ApplicationCommandGroupData(
        data.name,
        data.description,
        data.options.orEmpty().map { ApplicationCommandSubCommandData(it) }
    )
}


@Serializable
public data class ApplicationCommandSubcommandData(
    val name: String,
    val description: String,
    val isDefault: OptionalBoolean,
    val parameters: Optional<List<ApplicationCommandParameterData>>
)


@Suppress("FunctionName")
public fun ApplicationCommandSubCommandData(data: ApplicationCommandOptionData): ApplicationCommandSubcommandData {
    return ApplicationCommandSubcommandData(
        data.name,
        data.description,
        data.default,
        data.options.mapList { ApplicationCommandParameterData(it) }
    )
}



@Serializable
public data class ApplicationCommandParameterData(
    val name: String,
    val description: String,
    val required: OptionalBoolean,
    val choices: Optional<List<ApplicationCommandOptionChoiceData>>,
    val channelTypes: Optional<List<ChannelType>>
)


@Suppress("FunctionName")
public fun ApplicationCommandParameterData(data: ApplicationCommandOptionData): ApplicationCommandParameterData {
    return ApplicationCommandParameterData(
        data.name,
        data.description,
        data.required,
        data.choices,
        data.channelTypes
    )
}

@Serializable

public data class ApplicationCommandOptionChoiceData(
    val name: String,
    val value: String
) {
    public companion object {
        public fun from(choice: Choice<*>): ApplicationCommandOptionChoiceData {
            return with(choice) {
                ApplicationCommandOptionChoiceData(name, value.toString())
            }
        }
    }
}
