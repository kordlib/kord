package dev.kord.core

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionsData
import dev.kord.core.entity.application.*
import dev.kord.rest.builder.interaction.*
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

class SlashCommands(
    val applicationId: Snowflake,
    val service: InteractionService,
) {
    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalChatInputCommand(
        name: String,
        description: String,
        builder: ChatInputCreateBuilder.() -> Unit = {},
    ): GlobalChatInputCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = ChatInputCreateBuilder(name, description).apply(builder).toRequest()
        val response = service.createGlobalApplicationCommand(applicationId, request)
        val data = ApplicationCommandData.from(response)
        return GlobalChatInputCommand(data, service)
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalMessageCommand(
        name: String,
        builder: MessageCommandCreateBuilder.() -> Unit = {},
    ): GlobalMessageCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = MessageCommandCreateBuilder(name).apply(builder).toRequest()
        val response = service.createGlobalApplicationCommand(applicationId, request)
        val data = ApplicationCommandData.from(response)
        return GlobalMessageCommand(data, service)
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalUserCommand(
        name: String,
        builder: UserCommandCreateBuilder.() -> Unit = {},
    ): GlobalUserCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = UserCommandCreateBuilder(name).apply(builder).toRequest()
        val response = service.createGlobalApplicationCommand(applicationId, request)
        val data = ApplicationCommandData.from(response)
        return GlobalUserCommand(data, service)
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalApplicationCommands(
        builder: MultiApplicationCommandBuilder.() -> Unit,
    ): Flow<GlobalApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = MultiApplicationCommandBuilder().apply(builder).build()
        val commands = service.createGlobalApplicationCommands(applicationId, request)
        return flow {
            commands.forEach {
                val data = ApplicationCommandData.from(it)
                emit(GlobalApplicationCommand(data, service))
            }
        }
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildChatInputCommand(
        guildId: Snowflake,
        name: String,
        description: String,
        builder: ChatInputCreateBuilder.() -> Unit = {},
    ): GuildChatInputCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = ChatInputCreateBuilder(name, description).apply(builder).toRequest()
        val response = service.createGuildApplicationCommand(applicationId, guildId, request)
        val data = ApplicationCommandData.from(response)
        return GuildChatInputCommand(data, service)
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildMessageCommand(
        guildId: Snowflake,
        name: String,
        builder: MessageCommandCreateBuilder.() -> Unit = {},
    ): GuildMessageCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = MessageCommandCreateBuilder(name).apply(builder).toRequest()
        val response = service.createGuildApplicationCommand(applicationId, guildId, request)
        val data = ApplicationCommandData.from(response)
        return GuildMessageCommand(data, service)
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildUserCommand(
        guildId: Snowflake,
        name: String,
        builder: UserCommandCreateBuilder.() -> Unit = {},
    ): GuildUserCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = UserCommandCreateBuilder(name).apply(builder).toRequest()
        val response = service.createGuildApplicationCommand(applicationId, guildId, request)
        val data = ApplicationCommandData.from(response)
        return GuildUserCommand(data, service)
    }

    suspend fun getGuildApplicationCommand(guildId: Snowflake, commandId: Snowflake): GuildApplicationCommand {
        val response = service.getGuildCommand(applicationId, guildId, commandId)
        val data = ApplicationCommandData.from(response)
        return GuildApplicationCommand(data, service)
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildApplicationCommands(
        guildId: Snowflake,
        builder: MultiApplicationCommandBuilder.() -> Unit,
    ): Flow<GuildApplicationCommand> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = MultiApplicationCommandBuilder().apply(builder).build()

        val commands = service.createGuildApplicationCommands(applicationId, guildId, request)
        return flow {
            commands.forEach {
                val data = ApplicationCommandData.from(it)
                emit(GuildApplicationCommand(data, service))
            }
        }
    }

    fun getGuildApplicationCommands(guildId: Snowflake): Flow<GuildApplicationCommand> = flow {
        for (command in service.getGuildApplicationCommands(applicationId, guildId)) {
            val data = ApplicationCommandData.from(command)
            emit(GuildApplicationCommand(data, service))
        }
    }


    suspend fun getGlobalApplicationCommand(commandId: Snowflake): GlobalApplicationCommand {
        val response = service.getGlobalCommand(applicationId, commandId)
        val data = ApplicationCommandData.from(response)
        return GlobalApplicationCommand(data, service)
    }

    fun getGlobalApplicationCommands(): Flow<GlobalApplicationCommand> = flow {
        for (command in service.getGlobalApplicationCommands(applicationId)) {
            val data = ApplicationCommandData.from(command)
            emit(GlobalApplicationCommand(data, service))
        }
    }

    suspend fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): ApplicationCommandPermissions {
        val permissions = service.getGuildApplicationCommandPermissions(applicationId, guildId)
        val data = GuildApplicationCommandPermissionsData.from(permissions)

        return ApplicationCommandPermissions(data)
    }

    suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): ApplicationCommandPermissions {
        val permissions = service.getApplicationCommandPermissions(applicationId, guildId, commandId)
        val data = GuildApplicationCommandPermissionsData.from(permissions)

        return ApplicationCommandPermissions(data)
    }

    suspend fun editApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit,
    ) {
        val request = ApplicationCommandPermissionsModifyBuilder().apply(builder).toRequest()

        service.editApplicationCommandPermissions(applicationId, guildId, commandId, request)
    }

    suspend fun bulkEditApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        builder: ApplicationCommandPermissionsBulkModifyBuilder.() -> Unit,
    ) {
        val request = ApplicationCommandPermissionsBulkModifyBuilder().apply(builder).toRequest()

        service.bulkEditApplicationCommandPermissions(applicationId, guildId, request)
    }
}


fun SlashCommands(applicationId: Snowflake, requestHandler: RequestHandler) =
    SlashCommands(applicationId, InteractionService(requestHandler))
