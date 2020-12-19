package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.mapList
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationCommandData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val name: String,
    val description: String,
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
                    options.mapList { ApplicationCommandOptionData.from(it) })
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
data class ApplicationCommandOptionChoiceData(
    val name: String,
    val value: String
) {
    companion object {
        fun from(choice: DiscordApplicationCommandOptionChoice): ApplicationCommandOptionChoiceData {
            return with(choice) {
                ApplicationCommandOptionChoiceData(name, value)
            }
        }
    }
}