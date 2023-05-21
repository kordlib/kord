package dev.kord.rest.service

import dev.kord.common.entity.*
import dev.kord.common.entity.MessageFlag.Ephemeral
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.*
import dev.kord.rest.builder.interaction.*
import dev.kord.rest.builder.message.create.FollowupMessageCreateBuilder
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.route.Route
import dev.kord.rest.route.Routes
import dev.kord.rest.route.Routes.Applications.ById.Guilds.ById.Commands.ById.Permissions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class InteractionService(public val client: HttpClient) {

    public suspend fun getGlobalApplicationCommands(
        applicationId: Snowflake,
        withLocalizations: Boolean? = null
    ): List<DiscordApplicationCommand> =
        client.get(Routes.Applications.ById.Commands(applicationId)) {
            withLocalizations?.let { parameter("with_localizations", it) }
        }.body()

    public suspend fun createGlobalApplicationCommand(
        applicationId: Snowflake,
        request: ApplicationCommandCreateRequest
    ): DiscordApplicationCommand =
        client.post(Routes.Applications.ById.Commands(applicationId)) {
            setBody(request)
        }.body()

    public suspend fun createGlobalApplicationCommands(
        applicationId: Snowflake,
        request: List<ApplicationCommandCreateRequest>
    ): List<DiscordApplicationCommand> =
        client.post(Routes.Applications.ById.Commands(applicationId)) {
            setBody(request)
        }.body()

    public suspend fun modifyGlobalApplicationCommand(
        applicationId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest,
    ): DiscordApplicationCommand = client.patch(Routes.Applications.ById.Commands.ById(applicationId, commandId)) {
        setBody(request)
    }.body()

    public suspend fun deleteGlobalApplicationCommand(applicationId: Snowflake, commandId: Snowflake): Unit {
        client.delete(Routes.Applications.ById.Commands.ById(applicationId, commandId))
    }

    public suspend fun getGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        withLocalizations: Boolean? = null
    ): List<DiscordApplicationCommand> =
        client.get(Routes.Applications.ById.Guilds.ById.Commands(applicationId, guildId)) {
            withLocalizations?.let { parameter("with_localizations", it) }
        }.body()

    public suspend fun createGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: ApplicationCommandCreateRequest,
    ): DiscordApplicationCommand =
        client.post(Routes.Applications.ById.Guilds.ById.Commands(applicationId, guildId)) {
            setBody(request)
        }.body()

    public suspend fun createGuildApplicationCommands(
        applicationId: Snowflake,
        guildId: Snowflake,
        request: List<ApplicationCommandCreateRequest>,
    ): List<DiscordApplicationCommand> =
        client.post(Routes.Applications.ById.Guilds.ById.Commands(applicationId, guildId)) {
            setBody(request)
        }.body()


    public suspend fun modifyGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
        request: ApplicationCommandModifyRequest,
    ): DiscordApplicationCommand =
        client.patch(Routes.Applications.ById.Guilds.ById.Commands.ById(applicationId, guildId, commandId)) {
            setBody(request)
        }.body()

    public suspend fun deleteGuildApplicationCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ) {
        client.delete(Routes.Applications.ById.Guilds.ById.Commands.ById(applicationId, guildId, commandId))
    }

    public suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: MultipartInteractionResponseCreateRequest,
    ) {
        client.post(Routes.Interactions.ById.Token.Callback(interactionId, interactionToken)) {
            setBody(request.request)
            request.files.orEmpty().onEach { file(it) }
        }
    }

    public suspend fun createInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        request: InteractionResponseCreateRequest,
    ) {
        client.post(Routes.Interactions.ById.Token.Callback(interactionId, interactionToken)) {
            setBody(request)
        }
    }

    public suspend inline fun <reified T> createAutoCompleteInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        autoComplete: DiscordAutoComplete<T>,
        typeSerializer: KSerializer<T> = serializer(),
    ) {
        client.post(Routes.Interactions.ById.Token.Callback(interactionId, interactionToken)) {
            setBody(
                AutoCompleteResponseCreateRequest(
                    InteractionResponseType.ApplicationCommandAutoCompleteResult,
                    autoComplete
                )
            )
        }
    }

    public suspend fun createModalInteractionResponse(
        interactionId: Snowflake,
        interactionToken: String,
        modal: DiscordModal
    ) {
        client.post(Routes.Interactions.ById.Token.Callback(interactionId, interactionToken)) {
            setBody(ModalResponseCreateRequest(InteractionResponseType.Modal, modal))
        }
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
        client.get(Routes.Webhooks.ById.WithToken.Messages.Original(applicationId, interactionToken)).body()

    public suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        multipartRequest: MultipartInteractionResponseModifyRequest,
    ): DiscordMessage =
        client.patch(Routes.Webhooks.ById.WithToken.Messages.Original(applicationId, interactionToken)) {
        setBody(multipartRequest.request)
        multipartRequest.files.orEmpty().forEach { file(it) }
    }.body()

    public suspend fun modifyInteractionResponse(
        applicationId: Snowflake,
        interactionToken: String,
        request: InteractionResponseModifyRequest,
    ): DiscordMessage = client.patch(Routes.Webhooks.ById.WithToken.Messages.Original(applicationId, interactionToken)) {
        setBody(request)
    }.body()

    public suspend fun deleteOriginalInteractionResponse(applicationId: Snowflake, interactionToken: String) {
        client.delete(Routes.Webhooks.ById.WithToken.Messages.Original(applicationId, interactionToken))
    }

    public suspend fun createFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        multipart: MultipartFollowupMessageCreateRequest,
    ): DiscordMessage = client.post(Routes.Webhooks.ById.WithToken.Messages(applicationId, interactionToken)) {
        setBody(multipart.request)
        multipart.files.forEach { file(it) }
    }.body()

    public suspend fun getFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ): DiscordMessage =
        client.get(Routes.Webhooks.ById.WithToken.Messages.ById(applicationId, interactionToken, messageId)).body()

    public suspend fun deleteFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
    ) {
        client.delete(Routes.Webhooks.ById.WithToken.Messages.ById(applicationId, interactionToken, messageId))
    }

    public suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: MultipartFollowupMessageModifyRequest,
    ): DiscordMessage =
        client.patch(Routes.Webhooks.ById.WithToken.Messages.ById(applicationId, interactionToken, messageId)) {
        setBody(request.request)
        request.files.orEmpty().forEach { file(it) }
    }.body()

    public suspend fun modifyFollowupMessage(
        applicationId: Snowflake,
        interactionToken: String,
        messageId: Snowflake,
        request: FollowupMessageModifyRequest,
    ): DiscordMessage = client.patch(Routes.Webhooks.ById.WithToken.Messages.ById(applicationId, interactionToken, messageId)) {
        setBody(request)
    }.body()

    public suspend fun getGlobalCommand(applicationId: Snowflake, commandId: Snowflake): DiscordApplicationCommand =
        client.get(Routes.Applications.ById.Commands.ById(applicationId, commandId)).body()

    public suspend fun getGuildCommand(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordApplicationCommand =
        client.get(Routes.Applications.ById.Guilds.ById.Commands.ById(applicationId, guildId, commandId)).body()
    )
    public suspend fun getGuildApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
    ): List<DiscordGuildApplicationCommandPermissions> =
        client.get(Routes.Applications.ById.Guilds.ById.Permissions(applicationId, guildId)).body()

    public suspend fun getApplicationCommandPermissions(
        applicationId: Snowflake,
        guildId: Snowflake,
        commandId: Snowflake,
    ): DiscordGuildApplicationCommandPermissions =
        client.get(Routes.Applications.ById.Guilds.ById.Commands.ById.Permissions(applicationId, guildId, commandId)).body()

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
