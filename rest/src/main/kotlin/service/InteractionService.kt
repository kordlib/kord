package dev.kord.rest.service

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.interaction.*
import dev.kord.rest.builder.message.create.EphemeralFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.EphemeralInteractionResponseCreateBuilder
import dev.kord.rest.builder.message.create.PublicFollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.PublicInteractionResponseCreateBuilder
import dev.kord.rest.builder.message.modify.EphemeralFollowupMessageModifyBuilder
import dev.kord.rest.builder.message.modify.EphemeralInteractionResponseModifyBuilder
import dev.kord.rest.builder.message.modify.PublicFollowupMessageModifyBuilder
import dev.kord.rest.builder.message.modify.PublicInteractionResponseModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlinx.serialization.builtins.ListSerializer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


class InteractionService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getGlobalApplicationCommands(applicationId: Snowflake): List<DiscordApplicationCommand> =
        call(Route.GlobalApplicationCommandsGet) {
            keys[Route.ApplicationId] = applicationId
        }

    suspend fun createGlobalApplicationCommand(
        applicationId: Snowflake,
        request: ApplicationCommandCreateRequest
    ): DiscordApplicationCommand = call(Route.GlobalApplicationCommandCreate) {
        keys[Route.ApplicationId] = applicationId
        body(ApplicationCommandCreateRequest.serializer(), request)
    }

    suspend fun createGlobalApplicationCommands(
        applicationId: Snowflake,
        request: List<ApplicationCommandCreateRequest>
    ): List<DiscordApplicationCommand> = call(Route.GlobalApplicationCommandsCreate) {
        keys[Route.ApplicationId] = applicationId
        body(ListSerializer(ApplicationCommandCreateRequest.serializer()), request)
    }

    suspend fun modifyGlobalApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest
    ) = call(Route.GlobalApplicationCommandModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.CommandId] = commandId
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    suspend fun deleteGlobalApplicationCommand(applicationId: Snowflake, commandId: Snowflake) =
        call(Route.GlobalApplicationCommandDelete) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.CommandId] = commandId
        }

    suspend fun getGuildApplicationCommands(applicationId: Snowflake, guildId: Snowflake) =
        call(Route.GuildApplicationCommandsGet) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.GuildId] = guildId
        }

    suspend fun createGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: ApplicationCommandCreateRequest
    ) = call(Route.GuildApplicationCommandCreate) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        body(ApplicationCommandCreateRequest.serializer(), request)
    }

    suspend fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<ApplicationCommandCreateRequest>
    ) = call(Route.GuildApplicationCommandsCreate) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        body(ListSerializer(ApplicationCommandCreateRequest.serializer()), request)
    }


    suspend fun modifyGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest
    ) = call(Route.GuildApplicationCommandModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    suspend fun deleteGuildApplicationCommand(applicationId: Snowflake, guildId: Snowflake, commandId: Snowflake) =
        call(Route.GuildApplicationCommandDelete) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.GuildId] = guildId
            keys[Route.CommandId] = commandId
        }

    suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: MultipartInteractionResponseCreateRequest
    ) = call(Route.InteractionResponseCreate) {
        keys[Route.InteractionId] = interactionId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseCreateRequest.serializer(), request.request)
        request.files.orEmpty().onEach { file(it) }
    }


    suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: InteractionResponseCreateRequest
    ) = call(Route.InteractionResponseCreate) {
        keys[Route.InteractionId] = interactionId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseCreateRequest.serializer(), request)
    }

    suspend fun getInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
    ) = call(Route.OriginalInteractionResponseGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
    }

    suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        multipartRequest: MultipartInteractionResponseModifyRequest
    ) = call(Route.OriginalInteractionResponseModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseModifyRequest.serializer(), multipartRequest.request)
        multipartRequest.files.orEmpty().forEach { file(it) }
    }

    suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        request: InteractionResponseModifyRequest
    ) = call(Route.OriginalInteractionResponseModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseModifyRequest.serializer(), request)
    }


    suspend fun deleteOriginalInteractionResponse(applicationId: Snowflake, interactionToken: String) =
        call(Route.OriginalInteractionResponseDelete) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.InteractionToken] = interactionToken
        }

    suspend fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        multipart: MultipartFollowupMessageCreateRequest,
    ) = call(Route.FollowupMessageCreate) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        body(FollowupMessageCreateRequest.serializer(), multipart.request)
        multipart.files.forEach { file(it) }

    }

    suspend fun deleteFollowupMessage(applicationId: Snowflake, interactionToken: String, messageId: Snowflake) =
        call(Route.FollowupMessageDelete) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.InteractionToken] = interactionToken
            keys[Route.MessageId] = messageId
        }

    suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: MultipartFollowupMessageModifyRequest
    ) = call(Route.FollowupMessageModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        keys[Route.MessageId] = messageId
        body(FollowupMessageModifyRequest.serializer(), request.request)
        request.files.orEmpty().forEach { file(it) }
    }


    suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: FollowupMessageModifyRequest
    ) = call(Route.FollowupMessageModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        keys[Route.MessageId] = messageId
        body(FollowupMessageModifyRequest.serializer(), request)
    }

    suspend fun getGlobalCommand(applicationId: Snowflake, commandId: Snowflake) =
        call(Route.GlobalApplicationCommandGet) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.CommandId] = commandId
        }


    suspend fun getGuildCommand(applicationId: Snowflake, guildId: Snowflake, commandId: Snowflake) =
        call(Route.GuildApplicationCommandGet) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.GuildId] = guildId
            keys[Route.CommandId] = commandId
        }

    suspend fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): List<DiscordGuildApplicationCommandPermissions> = call(Route.GuildApplicationCommandPermissionsGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
    }

    suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordGuildApplicationCommandPermissions = call(Route.ApplicationCommandPermissionsGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId
    }

    suspend fun editApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandPermissionsEditRequest,
    ) = call(Route.ApplicationCommandPermissionsPut) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId

        body(ApplicationCommandPermissionsEditRequest.serializer(), request)
    }

    suspend fun bulkEditApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<PartialDiscordGuildApplicationCommandPermissions>,
    ) = call(Route.ApplicationCommandPermissionsBatchPut) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId

        body(ListSerializer(PartialDiscordGuildApplicationCommandPermissions.serializer()), request)
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalChatInputApplicationCommand(
        applicationId: Snowflake,
        name: String,
        description: String,
        builder: ChatInputCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createGlobalApplicationCommand(
            applicationId,
            ChatInputCreateBuilder(name, description).apply(builder).toRequest()
        )
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalMessageCommandApplicationCommand(
        applicationId: Snowflake,
        name: String,
        builder: MessageCommandCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommand(
            applicationId,
            MessageCommandCreateBuilder(name).apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalUserCommandApplicationCommand(
        applicationId: Snowflake,
        name: String,
        builder: UserCommandCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommand(
            applicationId,
            UserCommandCreateBuilder(name).apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGlobalApplicationCommands(
        applicationId: Snowflake,
        builder: MultiApplicationCommandBuilder.() -> Unit
    ): List<DiscordApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommands(
            applicationId,
            MultiApplicationCommandBuilder().apply(builder).build()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGlobalChatInputApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        builder: ChatInputModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return modifyGlobalApplicationCommand(
            applicationId,
            commandId,
            ChatInputModifyBuilder().apply(builder).toRequest()
        )
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGlobalMessageApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        builder: MessageCommandModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return modifyGlobalApplicationCommand(
            applicationId,
            commandId,
            MessageCommandModifyBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGlobalUserApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        builder: UserCommandModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyGlobalApplicationCommand(
            applicationId,
            commandId,
            UserCommandModifyBuilder().apply(builder).toRequest()
        )
    }



    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildChatInputApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        name: String,
        description: String,
        builder: ChatInputCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createGuildApplicationCommand(
            applicationId,
            guildId,
            ChatInputCreateBuilder(name, description).apply(builder).toRequest()
        )
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildMessageCommandApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        name: String,
        builder: MessageCommandCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGuildApplicationCommand(
            applicationId,
            guildId,
            MessageCommandCreateBuilder(name).apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildUserCommandApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        name: String,
        builder: UserCommandCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGuildApplicationCommand(
            applicationId,
            guildId,
            UserCommandCreateBuilder(name).apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        builder: MultiApplicationCommandBuilder.() -> Unit
    ): List<DiscordApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGuildApplicationCommands(
            applicationId,
            guildId,
            MultiApplicationCommandBuilder().apply(builder).build()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildChatInputApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        builder: ChatInputModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return modifyGuildApplicationCommand(
            applicationId,
            guildId,
            commandId,
            ChatInputModifyBuilder().apply(builder).toRequest()
        )
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildMessageApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        builder: MessageCommandModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return modifyGuildApplicationCommand(
            applicationId,
            guildId,
            commandId,
            MessageCommandModifyBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyGuildUserApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        builder: UserCommandModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyGuildApplicationCommand(
            applicationId,
            guildId,
            commandId,
            UserCommandModifyBuilder().apply(builder).toRequest()
        )
    }


    suspend inline fun createPublicInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builder: PublicInteractionResponseCreateBuilder.() -> Unit
    ) {
        return createInteractionResponse(
            interactionId,
            interactionToken,
            PublicInteractionResponseCreateBuilder().apply(builder).toRequest()
        )
    }


    suspend inline fun createEphemeralInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builder: EphemeralInteractionResponseCreateBuilder.() -> Unit
    ) {
        return createInteractionResponse(
            interactionId,
            interactionToken,
            EphemeralInteractionResponseCreateBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyPublicInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        builder: PublicInteractionResponseModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyInteractionResponse(
            applicationId,
            interactionToken,
            PublicInteractionResponseModifyBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyEphemeralInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        builder: EphemeralInteractionResponseModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyInteractionResponse(
            applicationId,
            interactionToken,
            EphemeralInteractionResponseModifyBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createEphemeralFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        builder: EphemeralFollowupMessageCreateBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createFollowupMessage(
            applicationId,
            interactionToken,
            EphemeralFollowupMessageCreateBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        builder: PublicFollowupMessageCreateBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createFollowupMessage(
            applicationId,
            interactionToken,
            PublicFollowupMessageCreateBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyPublicFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        builder: PublicFollowupMessageModifyBuilder.() -> Unit = {}
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyFollowupMessage(
            applicationId,
            interactionToken,
            messageId,
            PublicFollowupMessageModifyBuilder().apply(builder).toRequest()
        )
    }


    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyEphemeralFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        builder: EphemeralFollowupMessageModifyBuilder.() -> Unit = {}
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyFollowupMessage(
            applicationId,
            interactionToken,
            messageId,
            EphemeralFollowupMessageModifyBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun bulkEditApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        builder: ApplicationCommandPermissionsBulkModifyBuilder.() -> Unit = {}
    ): List<DiscordGuildApplicationCommandPermissions> {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return bulkEditApplicationCommandPermissions(
            applicationId,
            guildId,
            ApplicationCommandPermissionsBulkModifyBuilder().apply(builder).toRequest()
        )
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun editApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit
    ): DiscordGuildApplicationCommandPermissions {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return editApplicationCommandPermissions(
            applicationId,
            guildId,
            commandId,
            ApplicationCommandPermissionsModifyBuilder().apply(builder).toRequest()
        )
    }

}
