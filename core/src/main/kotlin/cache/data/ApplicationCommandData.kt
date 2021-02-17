package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
@KordPreview
data class ApplicationCommandData(
        val id: Snowflake,
        val applicationId: Snowflake,
        val name: String,
        val description: String,
        val guildId: OptionalSnowflake,
        val options: Optional<List<ApplicationCommandOptionData>>
) {
    companion object {
        fun from(command: DiscordApplicationCommand): ApplicationCommandData {
            return with(command) {
                ApplicationCommandData(
                        id,
                        applicationId,
                        name,
                        description,
                        guildId,
                        options.mapList { ApplicationCommandOptionData.from(it) })
            }
        }
    }
}

@Serializable
@KordPreview
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


@KordPreview
@Serializable
class ApplicationCommandGroupData(
        val name: String,
        val description: String,
        val subCommands: List<ApplicationCommandSubcommandData>
)

@KordPreview
@Suppress("FunctionName")
fun ApplicationCommandGroupData(data: ApplicationCommandOptionData): ApplicationCommandGroupData {
    return ApplicationCommandGroupData(
            data.name,
            data.description,
            data.options.orEmpty().map { ApplicationCommandSubCommandData(it) }
    )
}


@KordPreview
@Serializable
data class ApplicationCommandSubcommandData(
        val name: String,
        val description: String,
        val isDefault: OptionalBoolean,
        val parameters: Optional<List<ApplicationCommandParameterData>>
)

@KordPreview
@Suppress("FunctionName")
fun ApplicationCommandSubCommandData(data: ApplicationCommandOptionData): ApplicationCommandSubcommandData {
    return ApplicationCommandSubcommandData(
            data.name,
            data.description,
            data.default,
            data.options.mapList { ApplicationCommandParameterData(it) }
    )
}


@KordPreview
@Serializable
data class ApplicationCommandParameterData(
        val name: String,
        val description: String,
        val required: OptionalBoolean,
        val choices: Optional<List<ApplicationCommandOptionChoiceData>>
)

@KordPreview
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
@KordPreview
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