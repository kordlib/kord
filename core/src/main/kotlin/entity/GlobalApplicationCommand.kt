package dev.kord.core.entity

import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.value
import dev.kord.core.behavior.GlobalApplicationCommandBehavior
import dev.kord.core.behavior.GuildApplicationCommandBehavior
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.ApplicationCommandOptionChoiceData
import dev.kord.core.cache.data.ApplicationCommandOptionData
import dev.kord.rest.service.InteractionService

class GlobalApplicationCommand(val data: ApplicationCommandData, override val service: InteractionService) :
    GlobalApplicationCommandBehavior {
    override val id: Snowflake
        get() = data.id

    override val applicationId: Snowflake
        get() = data.applicationId

    val name get() = data.name

    val description: String get() = data.description

    val options: List<CommandOptions>? = data.options.mapList { CommandOptions(it) }.value


}

class GuildApplicationCommand(val data: ApplicationCommandData,override val guildId: Snowflake, override val service: InteractionService) :
    GuildApplicationCommandBehavior {
    override val id: Snowflake
        get() = data.id

    override val applicationId: Snowflake
        get() = data.applicationId

    val name get() = data.name

    val description: String get() = data.description

    val options: List<CommandOptions>? = data.options.mapList { CommandOptions(it) }.value


}

data class CommandOptions(val data: ApplicationCommandOptionData) {
    val type: ApplicationCommandOptionType get() = data.type
    val name: String get() = data.name
    val description: String get() = data.description
    val default: Boolean? = data.default.value
    val required: Boolean? = data.required.value
    val choices: Map<String, String>? = data.choices.value?.associate { it.name to it.value }
    val options: List<CommandOptions>? = data.options.mapList { CommandOptions(it) }.value
}