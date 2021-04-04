package dev.kord.core

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.ApplicationCommandsCreateBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.service.InteractionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Represents Slash Command's rest-only endpoints.
 * This should be used only when registering new commands or modifying existing once.
 */
@KordPreview
class SlashCommands(
    val applicationId: Snowflake,
    val service: InteractionService,
) {
    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalApplicationCommand(
        name: String,
        description: String,
        builder: ApplicationCommandCreateBuilder.() -> Unit = {},
    ): GlobalApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = ApplicationCommandCreateBuilder(name, description).apply(builder).toRequest()
        val response = service.createGlobalApplicationCommand(applicationId, request)
        val data = ApplicationCommandData.from(response)
        return GlobalApplicationCommand(data, service)
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalApplicationCommands(
        builder: ApplicationCommandsCreateBuilder.() -> Unit,
    ): Flow<GlobalApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = ApplicationCommandsCreateBuilder().apply(builder).toRequest()

        return flow {
            for (command in service.createGlobalApplicationCommands(applicationId, request)) {
                val data = ApplicationCommandData.from(command)
                emit(GlobalApplicationCommand(data, service))
            }
        }
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildApplicationCommand(
        guildId: Snowflake,
        name: String,
        description: String,
        builder: ApplicationCommandCreateBuilder.() -> Unit = {},
    ): GuildApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = ApplicationCommandCreateBuilder(name, description).apply(builder).toRequest()
        val response = service.createGuildApplicationCommand(applicationId, guildId, request)
        val data = ApplicationCommandData.from(response)
        return GuildApplicationCommand(data, service, guildId)
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildApplicationCommands(
        guildId: Snowflake,
        builder: ApplicationCommandsCreateBuilder.() -> Unit,
    ): Flow<GuildApplicationCommand> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = ApplicationCommandsCreateBuilder().apply(builder).toRequest()

        return flow {
            for (command in service.createGuildApplicationCommands(applicationId, guildId, request)) {
                val data = ApplicationCommandData.from(command)
                emit(GuildApplicationCommand(data, service, guildId))
            }
        }
    }

    fun getGuildApplicationCommands(guildId: Snowflake): Flow<GuildApplicationCommand> = flow {
        for (command in service.getGuildApplicationCommands(applicationId, guildId)) {
            val data = ApplicationCommandData.from(command)
            emit(GuildApplicationCommand(data, service, guildId))
        }
    }


    fun getGlobalApplicationCommands(): Flow<GlobalApplicationCommand> = flow {
        for (command in service.getGlobalApplicationCommands(applicationId)) {
            val data = ApplicationCommandData.from(command)
            emit(GlobalApplicationCommand(data, service))
        }
    }
}

@KordPreview
fun SlashCommands(applicationId: Snowflake, requestHandler: RequestHandler) =
    SlashCommands(applicationId, InteractionService(requestHandler))
