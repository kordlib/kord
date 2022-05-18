package dev.kord.rest.service

import dev.kord.common.entity.*
import dev.kord.common.entity.MessageFlag.Ephemeral
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.interaction.*
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import kotlin.collections.set
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class InteractionService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getGlobalApplicationCommands(applicationId: Snowflake, withLocalizations: Boolean? = null): List<DiscordApplicationCommand> =
        call(Route.GlobalApplicationCommandsGet) {
            keys[Route.ApplicationId] = applicationId
            withLocalizations?.let { parameter("with_localizations", it) }
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
        applicationIdCommandId(applicationId, commandId)
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    public suspend fun deleteGlobalApplicationCommand(applicationId: Snowflake, commandId: Snowflake): Unit =
        call(Route.GlobalApplicationCommandDelete) {
            applicationIdCommandId(applicationId, commandId)
        }

    public suspend fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean? = null
    ): List<DiscordApplicationCommand> = call(Route.GuildApplicationCommandsGet) {
        applicationIdGuildId(applicationId, guildId)
        withLocalizations?.let { parameter("with_localizations", it) }
    }

    public suspend fun createGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: ApplicationCommandCreateRequest,
    ): DiscordApplicationCommand = call(Route.GuildApplicationCommandCreate) {
        applicationIdGuildId(applicationId, guildId)
        body(ApplicationCommandCreateRequest.serializer(), request)
    }

    public suspend fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<ApplicationCommandCreateRequest>,
    ): List<DiscordApplicationCommand> = call(Route.GuildApplicationCommandsCreate) {
        applicationIdGuildId(applicationId, guildId)
        body(ListSerializer(ApplicationCommandCreateRequest.serializer()), request)
    }

    public suspend fun modifyGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest,
    ): DiscordApplicationCommand = call(Route.GuildApplicationCommandModify) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
        body(ApplicationCommandModifyRequest.serializer(), request)
    }

    public suspend fun deleteGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): Unit = call(Route.GuildApplicationCommandDelete) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
    }

    public suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: MultipartInteractionResponseCreateRequest,
    ): Unit = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
        body(InteractionResponseCreateRequest.serializer(), request.request)
        request.files.orEmpty().onEach { file(it) }
    }

    public suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: InteractionResponseCreateRequest,
    ): Unit = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
        body(InteractionResponseCreateRequest.serializer(), request)
    }

    public suspend inline fun <reified T> createAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        autoComplete: DiscordAutoComplete<T>,
        typeSerializer: KSerializer<T> = serializer(),
    ): Unit = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
        body(
            AutoCompleteResponseCreateRequest.serializer(typeSerializer),
            AutoCompleteResponseCreateRequest(
                InteractionResponseType.ApplicationCommandAutoCompleteResult,
                autoComplete
            )
        )
    }

    public suspend fun createModalInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        modal: DiscordModal
    ): Unit = call(Route.InteractionResponseCreate) {
        interactionIdInteractionToken(interactionId, interactionToken)
        body(
            ModalResponseCreateRequest.serializer(),
            ModalResponseCreateRequest(
                InteractionResponseType.Modal,
                modal
            )
        )
    }

    public suspend inline fun createModalInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        title: String,
        customId: String,
        builder: ModalBuilder.() -> Unit,
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return createModalInteractionResponse(
            interactionId,
            interactionToken,
            ModalBuilder(title, customId).apply(builder).toRequest()
        )
    }

    public suspend inline fun createIntAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builderFunction: IntegerOptionBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builderFunction, InvocationKind.EXACTLY_ONCE)
        }

        return createBuilderAutoCompleteInteractionResponse(
            interactionId,
            interactionToken,
            IntegerOptionBuilder("<auto-complete>", ""),
            builderFunction
        )
    }

    public suspend inline fun createNumberAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builderFunction: NumberOptionBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builderFunction, InvocationKind.EXACTLY_ONCE)
        }

        return createBuilderAutoCompleteInteractionResponse(
            interactionId,
            interactionToken,
            NumberOptionBuilder("<auto-complete>", ""),
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

    public suspend inline fun <reified T, Builder : BaseChoiceBuilder<T>> createBuilderAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        builder: Builder,
        builderFunction: Builder.() -> Unit
    ) {
        // TODO We can remove this cast when we change the type of BaseChoiceBuilder.choices to MutableList<Choice<T>>.
        //  This can be done once https://youtrack.jetbrains.com/issue/KT-51045 is fixed.
        //  Until then this cast is necessary to get the right serializer through reified generics.
        @Suppress("UNCHECKED_CAST")
        val choices = (builder.apply(builderFunction).choices ?: emptyList()) as List<Choice<T>>

        return createAutoCompleteInteractionResponse(interactionId, interactionToken, DiscordAutoComplete(choices))
    }

    public suspend fun getInteractionResponse(applicationId: Snowflake, interactionToken: String): DiscordMessage =
        call(Route.OriginalInteractionResponseGet) {
            applicationIdInteractionToken(applicationId, interactionToken)
        }

    public suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        multipartRequest: MultipartInteractionResponseModifyRequest,
    ): DiscordMessage = call(Route.OriginalInteractionResponseModify) {
        applicationIdInteractionToken(applicationId, interactionToken)
        body(InteractionResponseModifyRequest.serializer(), multipartRequest.request)
        multipartRequest.files.orEmpty().forEach { file(it) }
    }

    public suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        request: InteractionResponseModifyRequest,
    ): DiscordMessage = call(Route.OriginalInteractionResponseModify) {
        applicationIdInteractionToken(applicationId, interactionToken)
        body(InteractionResponseModifyRequest.serializer(), request)
    }

    public suspend fun deleteOriginalInteractionResponse(applicationId: Snowflake, interactionToken: String): Unit =
        call(Route.OriginalInteractionResponseDelete) {
            applicationIdInteractionToken(applicationId, interactionToken)
        }

    public suspend fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        multipart: MultipartFollowupMessageCreateRequest,
    ): DiscordMessage = call(Route.FollowupMessageCreate) {
        applicationIdInteractionToken(applicationId, interactionToken)
        body(FollowupMessageCreateRequest.serializer(), multipart.request)
        multipart.files.forEach { file(it) }
    }

    public suspend fun getFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): DiscordMessage = call(Route.FollowupMessageGet) {
        applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
    }

    public suspend fun deleteFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): Unit = call(Route.FollowupMessageDelete) {
        applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
    }

    public suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: MultipartFollowupMessageModifyRequest,
    ): DiscordMessage = call(Route.FollowupMessageModify) {
        applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
        body(FollowupMessageModifyRequest.serializer(), request.request)
        request.files.orEmpty().forEach { file(it) }
    }

    public suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: FollowupMessageModifyRequest,
    ): DiscordMessage = call(Route.FollowupMessageModify) {
        applicationIdInteractionTokenMessageId(applicationId, interactionToken, messageId)
        body(FollowupMessageModifyRequest.serializer(), request)
    }

    public suspend fun getGlobalCommand(applicationId: Snowflake, commandId: Snowflake): DiscordApplicationCommand =
        call(Route.GlobalApplicationCommandGet) {
            applicationIdCommandId(applicationId, commandId)
        }

    public suspend fun getGuildCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordApplicationCommand = call(Route.GuildApplicationCommandGet) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
    }

    public suspend fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): List<DiscordGuildApplicationCommandPermissions> = call(Route.GuildApplicationCommandPermissionsGet) {
        applicationIdGuildId(applicationId, guildId)
    }

    public suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordGuildApplicationCommandPermissions = call(Route.ApplicationCommandPermissionsGet) {
        applicationIdGuildIdCommandId(applicationId, guildId, commandId)
    }

    public suspend inline fun createGlobalChatInputApplicationCommand(
        applicationId: Snowflake,
        name: String,
        description: String,
        builder: GlobalChatInputCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createGlobalApplicationCommand(
            applicationId,
            ChatInputCreateBuilderImpl(name, description).apply(builder).toRequest()
        )
    }

    public suspend inline fun createGlobalMessageCommandApplicationCommand(
        applicationId: Snowflake,
        name: String,
        builder: GlobalMessageCommandCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommand(
            applicationId,
            MessageCommandCreateBuilderImpl(name).apply(builder).toRequest()
        )
    }

    public suspend inline fun createGlobalUserCommandApplicationCommand(
        applicationId: Snowflake,
        name: String,
        builder: GlobalUserCommandCreateBuilder.() -> Unit = {}
    ): DiscordApplicationCommand {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommand(
            applicationId,
            UserCommandCreateBuilderImpl(name).apply(builder).toRequest()
        )
    }

    public suspend inline fun createGlobalApplicationCommands(
        applicationId: Snowflake,
        builder: GlobalMultiApplicationCommandBuilder.() -> Unit
    ): List<DiscordApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGlobalApplicationCommands(
            applicationId,
            GlobalMultiApplicationCommandBuilder().apply(builder).build()
        )
    }

    public suspend inline fun modifyGlobalChatInputApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        builder: GlobalChatInputModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return modifyGlobalApplicationCommand(
            applicationId,
            commandId,
            ChatInputModifyBuilderImpl().apply(builder).toRequest()
        )
    }

    public suspend inline fun modifyGlobalMessageApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        builder: GlobalMessageCommandModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return modifyGlobalApplicationCommand(
            applicationId,
            commandId,
            MessageCommandModifyBuilderImpl().apply(builder).toRequest()
        )
    }

    public suspend inline fun modifyGlobalUserApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        builder: GlobalUserCommandModifyBuilder.() -> Unit
    ): DiscordApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyGlobalApplicationCommand(
            applicationId,
            commandId,
            UserCommandModifyBuilderImpl().apply(builder).toRequest()
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
            ChatInputCreateBuilderImpl(name, description).apply(builder).toRequest()
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
            MessageCommandCreateBuilderImpl(name).apply(builder).toRequest()
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
            UserCommandCreateBuilderImpl(name).apply(builder).toRequest()
        )
    }

    public suspend inline fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        builder: GuildMultiApplicationCommandBuilder.() -> Unit
    ): List<DiscordApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        return createGuildApplicationCommands(
            applicationId,
            guildId,
            GuildMultiApplicationCommandBuilder().apply(builder).build()
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
            ChatInputModifyBuilderImpl().apply(builder).toRequest()
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
            MessageCommandModifyBuilderImpl().apply(builder).toRequest()
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
            UserCommandModifyBuilderImpl().apply(builder).toRequest()
        )
    }


    @Deprecated(
        "'createPublicInteractionResponse' was renamed to 'createInteractionResponse'",
        ReplaceWith("this.createInteractionResponse(interactionId, interactionToken, ephemeral, builder)"),
        DeprecationLevel.ERROR,
    )
    public suspend inline fun createPublicInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        ephemeral: Boolean = false,
        builder: InteractionResponseCreateBuilder.() -> Unit
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createInteractionResponse(interactionId, interactionToken, ephemeral, builder)
    }

    public suspend inline fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        ephemeral: Boolean = false,
        builder: InteractionResponseCreateBuilder.() -> Unit,
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return createInteractionResponse(
            interactionId,
            interactionToken,
            InteractionResponseCreateBuilder(ephemeral).apply(builder).toRequest()
        )
    }

    public suspend inline fun modifyInteractionResponse(
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

    @Deprecated(
        "Renamed to 'deferMessage'.",
        ReplaceWith("this.deferMessage(interactionId, interactionToken, ephemeral)"),
    )
    public suspend fun acknowledge(interactionId: Snowflake, interactionToken: String, ephemeral: Boolean = false) {
        deferMessage(interactionId, interactionToken, ephemeral)
    }

    public suspend fun deferMessage(interactionId: Snowflake, interactionToken: String, ephemeral: Boolean = false) {
        val flags = if (ephemeral) MessageFlags(Ephemeral) else null
        val request = InteractionResponseCreateRequest(
            type = InteractionResponseType.DeferredChannelMessageWithSource,
            data = Optional(
                flags?.let { InteractionApplicationCommandCallbackData(flags = Optional(it)) }
            ).coerceToMissing()
        )
        createInteractionResponse(interactionId, interactionToken, request)
    }

    public suspend fun deferMessageUpdate(interactionId: Snowflake, interactionToken: String) {
        val request = InteractionResponseCreateRequest(type = InteractionResponseType.DeferredUpdateMessage)
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
