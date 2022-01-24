package dev.kord.rest.service

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.Choice
import dev.kord.common.entity.DiscordApplicationCommand
import dev.kord.common.entity.DiscordAutoComplete
import dev.kord.common.entity.DiscordGuildApplicationCommandPermissions
import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.PartialDiscordGuildApplicationCommandPermissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.interaction.ApplicationCommandPermissionsBulkModifyBuilder
import dev.kord.rest.builder.interaction.ApplicationCommandPermissionsModifyBuilder
import dev.kord.rest.builder.interaction.BaseChoiceBuilder
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.ChatInputModifyBuilder
import dev.kord.rest.builder.interaction.IntChoiceBuilder
import dev.kord.rest.builder.interaction.MessageCommandCreateBuilder
import dev.kord.rest.builder.interaction.MessageCommandModifyBuilder
import dev.kord.rest.builder.interaction.MultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.NumberChoiceBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import dev.kord.rest.builder.interaction.UserCommandCreateBuilder
import dev.kord.rest.builder.interaction.UserCommandModifyBuilder
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest
import dev.kord.rest.json.request.ApplicationCommandPermissionsEditRequest
import dev.kord.rest.json.request.AutoCompleteResponseCreateRequest
import dev.kord.rest.json.request.FollowupMessageCreateRequest
import dev.kord.rest.json.request.FollowupMessageModifyRequest
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import dev.kord.rest.json.request.InteractionResponseModifyRequest
import dev.kord.rest.json.request.MultipartFollowupMessageCreateRequest
import dev.kord.rest.json.request.MultipartFollowupMessageModifyRequest
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
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

    suspend inline fun <reified T> createAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        autoComplete: DiscordAutoComplete<T>,
        typeSerializer: KSerializer<T> = serializer()
    ) = call(Route.InteractionResponseCreate) {
        keys[Route.InteractionId] = interactionId
        keys[Route.InteractionToken] = interactionToken

        body(
            AutoCompleteResponseCreateRequest.serializer(typeSerializer),
            AutoCompleteResponseCreateRequest(
                InteractionResponseType.ApplicationCommandAutoCompleteResult,
                autoComplete
            )
        )
    }

    @PublishedApi
    internal suspend inline fun <reified T, Builder : BaseChoiceBuilder<T>> createBuilderAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builder: Builder,
        builderFunction: Builder.() -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        val choices = builder.apply(builderFunction).choices as List<Choice<T>>? ?: emptyList()

        return createAutoCompleteInteractionResponse(interactionId, interactionToken, DiscordAutoComplete(choices))
    }

    suspend inline fun createIntAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builderFunction: IntChoiceBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builderFunction, InvocationKind.EXACTLY_ONCE)
        }

        return createBuilderAutoCompleteInteractionResponse(
            interactionId,
            interactionToken,
            IntChoiceBuilder("<auto-complete>", ""),
            builderFunction
        )
    }

    suspend inline fun createNumberAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builderFunction: NumberChoiceBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builderFunction, InvocationKind.EXACTLY_ONCE)
        }

        return createBuilderAutoCompleteInteractionResponse(
            interactionId,
            interactionToken,
            NumberChoiceBuilder("<auto-complete>", ""),
            builderFunction
        )
    }

    suspend inline fun createStringAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builderFunction: StringChoiceBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builderFunction, InvocationKind.EXACTLY_ONCE)
        }

        return createBuilderAutoCompleteInteractionResponse(
            interactionId,
            interactionToken,
            StringChoiceBuilder("<auto-complete>", ""),
            builderFunction
        )
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
        ephemeral: Boolean = false,
        builder: InteractionResponseCreateBuilder.() -> Unit
    ) {
        return createInteractionResponse(
            interactionId,
            interactionToken,
            InteractionResponseCreateBuilder(ephemeral).apply(builder).toRequest()
        )
    }


    suspend inline fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        builder: InteractionResponseModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyInteractionResponse(
            applicationId,
            interactionToken,
            InteractionResponseModifyBuilder().apply(builder).toRequest()
        )
    }

    suspend inline fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        ephemeral: Boolean = false,
        builder: FollowupMessageCreateBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createFollowupMessage(
            applicationId,
            interactionToken,
            FollowupMessageCreateBuilder(ephemeral).apply(builder).toRequest()
        )
    }

    suspend inline fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        builder: FollowupMessageModifyBuilder.() -> Unit = {}
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyFollowupMessage(
            applicationId,
            interactionToken,
            messageId,
            FollowupMessageModifyBuilder().apply(builder).toRequest()
        )
    }


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

    public suspend fun acknowledge(interactionId: Snowflake, interactionToken: String, ephemeral: Boolean = false) {
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredChannelMessageWithSource,
            data = Optional(
                InteractionApplicationCommandCallbackData(
                    flags = Optional(if (ephemeral) MessageFlags(MessageFlag.Ephemeral) else null).coerceToMissing()
                )
            )
        )
        createInteractionResponse(interactionId, interactionToken, request)
    }

}
