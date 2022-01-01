package dev.kord.rest.service

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.interaction.*
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public class InteractionService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getGlobalApplicationCommands(applicationId: Snowflake): List<DiscordApplicationCommand> =
        call(Route.GlobalApplicationCommandsGet) {
            keys[Route.ApplicationId] = applicationId
        }

    public suspend fun createGlobalApplicationCommand(
        applicationId: Snowflake,
        request: ApplicationCommandCreateRequest
    ): DiscordApplicationCommand = call(Route.GlobalApplicationCommandCreate) {
        keys[Route.ApplicationId] = applicationId
        body(ApplicationCommandCreateRequest.serializer(), request)
    }

    public suspend fun createGlobalApplicationCommands(
        applicationId: Snowflake,
        request: List<ApplicationCommandCreateRequest>
    ): List<DiscordApplicationCommand> = call(Route.GlobalApplicationCommandsCreate) {
        keys[Route.ApplicationId] = applicationId
        body(ListSerializer(ApplicationCommandCreateRequest.serializer()), request)
    }

    public suspend fun modifyGlobalApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest,
    ): DiscordApplicationCommand = call(Route.GlobalApplicationCommandModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.CommandId] = commandId
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    public suspend fun deleteGlobalApplicationCommand(applicationId: Snowflake, commandId: Snowflake): Unit =
        call(Route.GlobalApplicationCommandDelete) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.CommandId] = commandId
        }

    public suspend fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): List<DiscordApplicationCommand> = call(Route.GuildApplicationCommandsGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
    }

    public suspend fun createGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: ApplicationCommandCreateRequest,
    ): DiscordApplicationCommand = call(Route.GuildApplicationCommandCreate) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        body(ApplicationCommandCreateRequest.serializer(), request)
    }

    public suspend fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<ApplicationCommandCreateRequest>,
    ): List<DiscordApplicationCommand> = call(Route.GuildApplicationCommandsCreate) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        body(ListSerializer(ApplicationCommandCreateRequest.serializer()), request)
    }

    public suspend fun modifyGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest,
    ): DiscordApplicationCommand = call(Route.GuildApplicationCommandModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    public suspend fun deleteGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): Unit = call(Route.GuildApplicationCommandDelete) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId
    }

    public suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: MultipartInteractionResponseCreateRequest,
    ): Unit = call(Route.InteractionResponseCreate) {
        keys[Route.InteractionId] = interactionId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseCreateRequest.serializer(), request.request)
        request.files.orEmpty().onEach { file(it) }
    }

    public suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: InteractionResponseCreateRequest,
    ): Unit = call(Route.InteractionResponseCreate) {
        keys[Route.InteractionId] = interactionId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseCreateRequest.serializer(), request)
    }

    public suspend inline fun <reified T> createAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        autoComplete: DiscordAutoComplete<T>,
        typeSerializer: KSerializer<T> = serializer(),
    ): Unit = call(Route.InteractionResponseCreate) {
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
        val choices = builder.apply(builderFunction).choices as List<Choice<T>>

        return createAutoCompleteInteractionResponse(interactionId, interactionToken, DiscordAutoComplete(choices))
    }

    public suspend inline fun createIntAutoCompleteInteractionResponse(
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

    public suspend inline fun createNumberAutoCompleteInteractionResponse(
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

    public suspend inline fun createStringAutoCompleteInteractionResponse(
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

    public suspend fun getInteractionResponse(applicationId: Snowflake, interactionToken: String): DiscordMessage =
        call(Route.OriginalInteractionResponseGet) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.InteractionToken] = interactionToken
        }

    public suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        multipartRequest: MultipartInteractionResponseModifyRequest,
    ): DiscordMessage = call(Route.OriginalInteractionResponseModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseModifyRequest.serializer(), multipartRequest.request)
        multipartRequest.files.orEmpty().forEach { file(it) }
    }

    public suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        request: InteractionResponseModifyRequest,
    ): DiscordMessage = call(Route.OriginalInteractionResponseModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        body(InteractionResponseModifyRequest.serializer(), request)
    }

    public suspend fun deleteOriginalInteractionResponse(applicationId: Snowflake, interactionToken: String): Unit =
        call(Route.OriginalInteractionResponseDelete) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.InteractionToken] = interactionToken
        }

    public suspend fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        multipart: MultipartFollowupMessageCreateRequest,
    ): DiscordMessage = call(Route.FollowupMessageCreate) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        body(FollowupMessageCreateRequest.serializer(), multipart.request)
        multipart.files.forEach { file(it) }
    }

    public suspend fun deleteFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): Unit = call(Route.FollowupMessageDelete) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        keys[Route.MessageId] = messageId
    }

    public suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: MultipartFollowupMessageModifyRequest,
    ): DiscordMessage = call(Route.FollowupMessageModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        keys[Route.MessageId] = messageId
        body(FollowupMessageModifyRequest.serializer(), request.request)
        request.files.orEmpty().forEach { file(it) }
    }

    public suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: FollowupMessageModifyRequest,
    ): DiscordMessage = call(Route.FollowupMessageModify) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.InteractionToken] = interactionToken
        keys[Route.MessageId] = messageId
        body(FollowupMessageModifyRequest.serializer(), request)
    }

    public suspend fun getGlobalCommand(applicationId: Snowflake, commandId: Snowflake): DiscordApplicationCommand =
        call(Route.GlobalApplicationCommandGet) {
            keys[Route.ApplicationId] = applicationId
            keys[Route.CommandId] = commandId
        }

    public suspend fun getGuildCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordApplicationCommand = call(Route.GuildApplicationCommandGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId
    }

    public suspend fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): List<DiscordGuildApplicationCommandPermissions> = call(Route.GuildApplicationCommandPermissionsGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
    }

    public suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordGuildApplicationCommandPermissions = call(Route.ApplicationCommandPermissionsGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId
    }

    public suspend fun editApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandPermissionsEditRequest,
    ): DiscordGuildApplicationCommandPermissions = call(Route.ApplicationCommandPermissionsPut) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId
        keys[Route.CommandId] = commandId

        body(ApplicationCommandPermissionsEditRequest.serializer(), request)
    }

    public suspend fun bulkEditApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<PartialDiscordGuildApplicationCommandPermissions>,
    ): List<DiscordGuildApplicationCommandPermissions> = call(Route.ApplicationCommandPermissionsBatchPut) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.GuildId] = guildId

        body(ListSerializer(PartialDiscordGuildApplicationCommandPermissions.serializer()), request)
    }

    public suspend inline fun createGlobalChatInputApplicationCommand(
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

    public suspend inline fun createGlobalMessageCommandApplicationCommand(
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

    public suspend inline fun createGlobalUserCommandApplicationCommand(
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

    public suspend inline fun createGlobalApplicationCommands(
        applicationId: Snowflake,
        builder: MultiApplicationCommandBuilder.() -> Unit
    ): List<DiscordApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommands(
            applicationId,
            MultiApplicationCommandBuilder().apply(builder).build()
        )
    }

    public suspend inline fun modifyGlobalChatInputApplicationCommand(
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

    public suspend inline fun modifyGlobalMessageApplicationCommand(
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

    public suspend inline fun modifyGlobalUserApplicationCommand(
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

    public suspend inline fun createGuildChatInputApplicationCommand(
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

    public suspend inline fun createGuildMessageCommandApplicationCommand(
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

    public suspend inline fun createGuildUserCommandApplicationCommand(
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

    public suspend inline fun createGuildApplicationCommands(
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

    public suspend inline fun modifyGuildChatInputApplicationCommand(
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

    public suspend inline fun modifyGuildMessageApplicationCommand(
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

    public suspend inline fun modifyGuildUserApplicationCommand(
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

    public suspend inline fun createPublicInteractionResponse(
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

    public suspend inline fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        builder: InteractionResponseModifyBuilder.() -> Unit,
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyInteractionResponse(
            applicationId,
            interactionToken,
            InteractionResponseModifyBuilder().apply(builder).toRequest()
        )
    }

    public suspend inline fun createFollowupMessage(
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

    public suspend inline fun modifyFollowupMessage(
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

    public suspend inline fun bulkEditApplicationCommandPermissions(
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

    public suspend inline fun editApplicationCommandPermissions(
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
