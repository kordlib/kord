package dev.kord.core

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.rest.builder.interaction.GlobalApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.GuildApplicationCommandCreateBuilder
import dev.kord.rest.service.InteractionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
@KordPreview
class SlashCommands(
    private val applicationId: Snowflake,
    private val service: InteractionService
) {
    suspend fun createGlobalApplicationCommand(
        name: String,
        description: String,
        builder: GlobalApplicationCommandCreateBuilder.() -> Unit = {}
    ): GlobalApplicationCommand {
        val request = GlobalApplicationCommandCreateBuilder(name, description).apply(builder).toRequest()
        val response = service.createGlobalApplicationCommand(applicationId, request)
        val data = ApplicationCommandData.from(response)
        return GlobalApplicationCommand(data, service)
    }

    suspend fun createGuildApplicationCommand(
        guildId: Snowflake,
        name: String,
        description: String,
        builder: GuildApplicationCommandCreateBuilder.() -> Unit = {}
    ): GuildApplicationCommand {
        val request = GuildApplicationCommandCreateBuilder(name, description).apply(builder).toRequest()
        val response = service.createGuildApplicationCommand(applicationId, guildId, request)
        val data = ApplicationCommandData.from(response)
        return GuildApplicationCommand(data, guildId, service)
    }

    suspend fun getGuildApplicationCommands(guildId: Snowflake): Flow<GuildApplicationCommand> = flow {
        for (command in service.getGuildApplicationCommands(applicationId, guildId)) {
            val data = ApplicationCommandData.from(command)
            emit(GuildApplicationCommand(data, guildId, service))
        }
    }


    suspend fun getGlobalApplicationCommands(): Flow<GlobalApplicationCommand> = flow {
        for (command in service.getGlobalApplicationCommands(applicationId)) {
            val data = ApplicationCommandData.from(command)
            emit(GlobalApplicationCommand(data, service))
        }
    }
}

fun Kord.toSlashCommands(applicationId: Snowflake): SlashCommands {
    return SlashCommands(applicationId, rest.interaction)
}