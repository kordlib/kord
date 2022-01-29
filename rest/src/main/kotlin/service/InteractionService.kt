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
import dev.kord.common.entity.MessageFlag.Ephemeral
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
import dev.kord.rest.request.RequestBuilder
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
        applicationIdCommandId(applicationId, commandId)
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    suspend fun deleteGlobalApplicationCommand(applicationId: Snowflake, commandId: Snowflake) =
        call(Route.GlobalApplicationCommandDelete) {
            applicationIdCommandId(applicationId, commandId)
        }

    suspend fun getGuildApplicationCommands(applicationId: Snowflake, guildId: Snowflake) =
        call(Route.GuildApplicationCommandsGet) {
            applicationIdGuildId(applicationId, guildId)
        }

    suspend fun createGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: ApplicationCommandCreateRequest
    ) = call(Route.GuildApplicationCommandCreate) {
        applicationIdGuildId(applicationId, guildId)
        body(ApplicationCommandCreateRequest.serializer(), request)
    }

    suspend fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<ApplicationCommandCreateRequest>
    ) = call(Route.GuildApplicationCommandsCreate) {
        applicationIdGuildId(applicationId, guildId)
        body(ListSerializer(ApplicationCommandCreateRequest.serializer()), request)
    }


    suspend fun modifyGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest
    ) = call(Route.GuildApplicationCommandModify) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    suspend fun deleteGuildApplicationCommand(applicationId: Snowflake, guildId: Snowflake, commandId: Snowflake) =
        call(Route.GuildApplicationCommandDelete) {
            applicationIdGuildIdCommandId(applicationId, guildId, commandId)
        }

    suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: MultipartInteractionResponseCreateRequest
    ) = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
        body(InteractionResponseCreateRequest.serializer(), request.request)
        request.files.orEmpty().onEach { file(it) }
    }


    suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: InteractionResponseCreateRequest
    ) = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
        body(InteractionResponseCreateRequest.serializer(), request)
    }

    suspend inline fun <reified T> createAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        autoComplete: DiscordAutoComplete<T>,
        typeSerializer: KSerializer<T> = serializer()
    ) = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
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
        val choices = builder.apply(builderFunction).choices ?: emptyList()

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
        applicationIdInteractionToken(applicationId, interactionToken)
    }

    suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        multipartRequest: MultipartInteractionResponseModifyRequest
    ) = call(Route.OriginalInteractionResponseModify) {
        applicationIdInteractionToken(applicationId, interactionToken)
        body(InteractionResponseModifyRequest.serializer(), multipartRequest.request)
        multipartRequest.files.orEmpty().forEach { file(it) }
    }

    suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        request: InteractionResponseModifyRequest
    ) = call(Route.OriginalInteractionResponseModify) {
        applicationIdInteractionToken(applicationId, interactionToken)
        body(InteractionResponseModifyRequest.serializer(), request)
    }


    suspend fun deleteOriginalInteractionResponse(applicationId: Snowflake, interactionToken: String) =
        call(Route.OriginalInteractionResponseDelete) {
            applicationIdInteractionToken(applicationId, interactionToken)
        }

    suspend fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        multipart: MultipartFollowupMessageCreateRequest,
    ) = call(Route.FollowupMessageCreate) {
        applicationIdInteractionToken(applicationId, interactionToken)
        body(FollowupMessageCreateRequest.serializer(), multipart.request)
        multipart.files.forEach { file(it) }

    }

    suspend fun getFollowupMessage(applicationId: Snowflake, interactionToken: String, messageId: Snowflake) =
        call(Route.FollowupMessageGet) {
            applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
        }

    suspend fun deleteFollowupMessage(applicationId: Snowflake, interactionToken: String, messageId: Snowflake) =
        call(Route.FollowupMessageDelete) {
            applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
        }

    suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: MultipartFollowupMessageModifyRequest
    ) = call(Route.FollowupMessageModify) {
        applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
        body(FollowupMessageModifyRequest.serializer(), request.request)
        request.files.orEmpty().forEach { file(it) }
    }


    suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: FollowupMessageModifyRequest
    ) = call(Route.FollowupMessageModify) {
        applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
        body(FollowupMessageModifyRequest.serializer(), request)
    }

    suspend fun getGlobalCommand(applicationId: Snowflake, commandId: Snowflake) =
        call(Route.GlobalApplicationCommandGet) {
            applicationIdCommandId(applicationId, commandId)
        }


    suspend fun getGuildCommand(applicationId: Snowflake, guildId: Snowflake, commandId: Snowflake) =
        call(Route.GuildApplicationCommandGet) {
            applicationIdGuildIdCommandId(applicationId, guildId, commandId)
        }

    suspend fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): List<DiscordGuildApplicationCommandPermissions> = call(Route.GuildApplicationCommandPermissionsGet) {
        applicationIdGuildId(applicationId, guildId)
    }

    suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordGuildApplicationCommandPermissions = call(Route.ApplicationCommandPermissionsGet) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
    }

    suspend fun editApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandPermissionsEditRequest,
    ) = call(Route.ApplicationCommandPermissionsPut) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
        body(ApplicationCommandPermissionsEditRequest.serializer(), request)
    }

    suspend fun bulkEditApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<PartialDiscordGuildApplicationCommandPermissions>,
    ) = call(Route.ApplicationCommandPermissionsBatchPut) {
        applicationIdGuildId(applicationId, guildId)
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


    @Deprecated(
        "'createPublicInteractionResponse' was renamed to 'createInteractionResponse'",
        ReplaceWith("this.createInteractionResponse(interactionId, interactionToken, ephemeral, builder)"),
        DeprecationLevel.ERROR,
    )
    suspend inline fun createPublicInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        ephemeral: Boolean = false,
        builder: InteractionResponseCreateBuilder.() -> Unit,
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createInteractionResponse(interactionId, interactionToken, ephemeral, builder)
    }

    suspend inline fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        ephemeral: Boolean = false,
        builder: InteractionResponseCreateBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
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
        val flags = if (ephemeral) MessageFlags(Ephemeral) else null
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredChannelMessageWithSource,
            data = Optional(
                flags?.let { InteractionApplicationCommandCallbackData(flags = Optional(it)) }
            ).coerceToMissing()
        )
        createInteractionResponse(interactionId, interactionToken, request)
    }

}

private fun RequestBuilder<*>.applicationIdCommandId(applicationId: Snowflake, commandId: Snowflake) {
    keys[Route.ApplicationId] = applicationId
    keys[Route.CommandId] = commandId
}
private fun RequestBuilder<*>.applicationIdGuildId(applicationId: Snowflake, guildId: Snowflake) {
    keys[Route.ApplicationId] = applicationId
    keys[Route.GuildId] = guildId
}

private fun RequestBuilder<*>.applicationIdGuildIdCommandId(
    applicationId: Snowflake,
    guildId: Snowflake,
    commandId: Snowflake,
) {
    applicationIdGuildId(applicationId, guildId)
    keys[Route.CommandId] = commandId
}

@PublishedApi
internal fun RequestBuilder<*>.interactionIdInteractionToken(interactionId: Snowflake, interactionToken: String) {
    keys[Route.InteractionId] = interactionId
    keys[Route.InteractionToken] = interactionToken
}

private fun RequestBuilder<*>.applicationIdInteractionToken(applicationId: Snowflake, interactionToken: String) {
    keys[Route.ApplicationId] = applicationId
    keys[Route.InteractionToken] = interactionToken
}

private fun RequestBuilder<*>.applicationIdInteractionTokenMessageId(
    applicationId: Snowflake,
    interactionToken: String,
    messageId: Snowflake,
) {
    applicationIdInteractionToken(applicationId, interactionToken)
    keys[Route.MessageId] = messageId
}
