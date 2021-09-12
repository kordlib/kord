package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.core.entity.application.ApplicationCommand
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationCommandData(
    val id: Snowflake,
    val type: Optional<ApplicationCommandType> = Optional.Missing(),
    val applicationId: Snowflake,
    val name: String,
    val description: String,
    val guildId: OptionalSnowflake,
    val options: Optional<List<ApplicationCommandOptionData>>,
    val defaultPermission: OptionalBoolean = OptionalBoolean.Missing,
    val version: Snowflake
) {
    companion object {
        val description = description(ApplicationCommandData::id) {
            link(ApplicationCommandData::guildId to GuildData::id)
        }
        fun from(command: DiscordApplicationCommand): ApplicationCommandData {
            return with(command) {
                ApplicationCommandData(
                    id,
                    type,
                    applicationId,
                    name,
                    description,
                    guildId,
                    options.mapList { ApplicationCommandOptionData.from(it) },
                    defaultPermission,
                    version
                )
            }
        }
    }
}

@Serializable
data class ApplicationCommandOptionData(
    val type: ApplicationCommandOptionType,
    val name: String,
    val description: String,
    val default: OptionalBoolean = OptionalBoolean.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    val choices: Optional<List<ApplicationCommandOptionChoiceData>> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOptionData>> = Optional.Missing()
) {
    companion object {
        fun from(data: ApplicationCommandOption): ApplicationCommandOptionData {
            return with(data) {
                ApplicationCommandOptionData(
                    type,
                    name,
                    description,
                    default,
                    required,
                    choices.mapList { ApplicationCommandOptionChoiceData.from(it) },
                    options.mapList { inner -> from(inner) }
                )
            }
        }
    }
}


@Serializable
class ApplicationCommandGroupData(
    val name: String,
    val description: String,
    val subCommands: List<ApplicationCommandSubcommandData>
)

@Suppress("FunctionName")
fun ApplicationCommandGroupData(data: ApplicationCommandOptionData): ApplicationCommandGroupData {
    return ApplicationCommandGroupData(
        data.name,
        data.description,
        data.options.orEmpty().map { ApplicationCommandSubCommandData(it) }
    )
}


@Serializable
data class ApplicationCommandSubcommandData(
    val name: String,
    val description: String,
    val isDefault: OptionalBoolean,
    val parameters: Optional<List<ApplicationCommandParameterData>>
)


@Suppress("FunctionName")
fun ApplicationCommandSubCommandData(data: ApplicationCommandOptionData): ApplicationCommandSubcommandData {
    return ApplicationCommandSubcommandData(
        data.name,
        data.description,
        data.default,
        data.options.mapList { ApplicationCommandParameterData(it) }
    )
}



@Serializable
data class ApplicationCommandParameterData(
    val name: String,
    val description: String,
    val required: OptionalBoolean,
    val choices: Optional<List<ApplicationCommandOptionChoiceData>>
)


@Suppress("FunctionName")
fun ApplicationCommandParameterData(data: ApplicationCommandOptionData): ApplicationCommandParameterData {
    return ApplicationCommandParameterData(
        data.name,
        data.description,
        data.required,
        data.choices
    )
}

@Serializable

data class ApplicationCommandOptionChoiceData(
    val name: String,
    val value: String
) {
    companion object {
        fun from(choice: Choice<*>): ApplicationCommandOptionChoiceData {
            return with(choice) {
                ApplicationCommandOptionChoiceData(name, value.toString())
            }
        }
    }
}