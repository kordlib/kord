package dev.kord.rest.route

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.rest.json.request.GuildScheduledEventUsersResponse
import dev.kord.rest.json.response.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import dev.kord.common.entity.DiscordEmoji as EmojiEntity

internal const val REST_VERSION_PROPERTY_NAME = "dev.kord.rest.version"
internal val restVersion get() = System.getenv(REST_VERSION_PROPERTY_NAME) ?: "v9"

sealed interface ResponseMapper<T> {
    fun deserialize(json: Json, body: String): T
}

internal class ValueJsonMapper<T>(val strategy: DeserializationStrategy<T>) : ResponseMapper<T> {
    override fun deserialize(json: Json, body: String): T = json.decodeFromString(strategy, body)
    override fun toString(): String = "ValueJsonMapper(strategy=$strategy)"
}

internal class NullAwareMapper<T>(val strategy: DeserializationStrategy<T>) : ResponseMapper<T?> {
    override fun deserialize(json: Json, body: String): T? {
        if (body.isBlank()) return null
        return json.decodeFromString(strategy, body)
    }

    override fun toString(): String = "NullAwareMapper(strategy=$strategy)"
}

internal val <T> DeserializationStrategy<T>.optional: ResponseMapper<T?>
    get() = NullAwareMapper(this)

sealed class Route<T>(
    val method: HttpMethod,
    val path: String,
    val mapper: ResponseMapper<T>,
    val requiresAuthorizationHeader: Boolean = true,
) {
    constructor(
        method: HttpMethod,
        path: String,
        strategy: DeserializationStrategy<T>,
        requiresAuthorizationHeader: Boolean = true,
    ) : this(method, path, ValueJsonMapper(strategy), requiresAuthorizationHeader)

    @OptIn(ExperimentalSerializationApi::class)
    override fun toString(): String =
        "Route(method:${method.value},path:$path,mapper:$mapper)"

    object GatewayGet :
        Route<GatewayResponse>(
            HttpMethod.Get,
            "/gateway",
            GatewayResponse.serializer(),
            requiresAuthorizationHeader = false,
        )

    object GatewayBotGet
        : Route<BotGatewayResponse>(HttpMethod.Get, "/gateway/bot", BotGatewayResponse.serializer())

    object AuditLogGet
        : Route<DiscordAuditLog>(HttpMethod.Get, "/guilds/$GuildId/audit-logs", DiscordAuditLog.serializer())

    object ChannelGet
        : Route<DiscordChannel>(HttpMethod.Get, "/channels/$ChannelId", DiscordChannel.serializer())

    object ChannelPut
        : Route<DiscordChannel>(HttpMethod.Put, "/channels/$ChannelId", DiscordChannel.serializer())

    object ChannelPatch
        : Route<DiscordChannel>(HttpMethod.Patch, "/channels/$ChannelId", DiscordChannel.serializer())

    object ChannelDelete
        : Route<DiscordChannel>(HttpMethod.Delete, "/channels/$ChannelId", DiscordChannel.serializer())

    object MessagePost
        : Route<DiscordMessage>(HttpMethod.Post, "/channels/$ChannelId/messages", DiscordMessage.serializer())

    object MessageGet
        : Route<DiscordMessage>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId", DiscordMessage.serializer())

    object MessagesGet
        : Route<List<DiscordMessage>>(
        HttpMethod.Get,
        "/channels/$ChannelId/messages",
        ListSerializer(DiscordMessage.serializer())
    )

    object PinsGet
        : Route<List<DiscordMessage>>(
        HttpMethod.Get,
        "/channels/$ChannelId/pins",
        ListSerializer(DiscordMessage.serializer())
    )

    object InvitesGet
        : Route<List<DiscordInvite>>(
        HttpMethod.Get,
        "/channels/$ChannelId/invites",
        ListSerializer(DiscordInvite.serializer())
    )

    object InvitePost
        : Route<DiscordInvite>(HttpMethod.Post, "/channels/$ChannelId/invites", DiscordInvite.serializer())

    object ReactionPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object OwnReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object ReactionDelete
        :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/$UserId", NoStrategy)

    object DeleteAllReactionsForEmoji
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji", NoStrategy)

    object MessageDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId", NoStrategy)

    object BulkMessageDeletePost
        : Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/messages/bulk-delete", NoStrategy)

    object PinDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/pins/$MessageId", NoStrategy)

    object PinPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/pins/$MessageId", NoStrategy)

    object AllReactionsDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions", NoStrategy)

    object ChannelPermissionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/permissions/$OverwriteId", NoStrategy)

    object ChannelPermissionPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/permissions/$OverwriteId", NoStrategy)

    object ReactionsGet
        : Route<List<DiscordUser>>(
        HttpMethod.Get,
        "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji",
        ListSerializer(DiscordUser.serializer())
    )

    object TypingIndicatorPost
        : Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/typing", NoStrategy)

    object GroupDMUserDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object GroupDMUserPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object EditMessagePatch
        :
        Route<DiscordMessage>(HttpMethod.Patch, "/channels/$ChannelId/messages/$MessageId", DiscordMessage.serializer())

    object GuildEmojiGet
        : Route<EmojiEntity>(HttpMethod.Get, "/guilds/$GuildId/emojis/$EmojiId", EmojiEntity.serializer())

    object GuildEmojisGet
        : Route<List<EmojiEntity>>(HttpMethod.Get, "/guilds/$GuildId/emojis", ListSerializer(EmojiEntity.serializer()))

    object GuildEmojiDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/emojis/$EmojiId", NoStrategy)

    object GuildEmojiPost
        : Route<EmojiEntity>(HttpMethod.Post, "/guilds/$GuildId/emojis", EmojiEntity.serializer())

    object GuildEmojiPatch
        : Route<EmojiEntity>(HttpMethod.Patch, "/guilds/$GuildId/emojis/$EmojiId", EmojiEntity.serializer())

    object InviteGet
        : Route<DiscordInvite>(HttpMethod.Get, "/invites/$InviteCode", DiscordInvite.serializer())

    object InviteDelete
        : Route<DiscordInvite>(HttpMethod.Delete, "/invites/$InviteCode", DiscordInvite.serializer())

    object CurrentUserGet
        : Route<DiscordUser>(HttpMethod.Get, "/users/@me", DiscordUser.serializer())

    object CurrentUserPatch
        : Route<DiscordUser>(HttpMethod.Patch, "/users/@me", DiscordUser.serializer())

    object UserGet
        : Route<DiscordUser>(HttpMethod.Get, "/users/$UserId", DiscordUser.serializer())

    object CurrentUsersGuildsGet
        : Route<List<DiscordPartialGuild>>(
        HttpMethod.Get,
        "/users/@me/guilds",
        ListSerializer(DiscordPartialGuild.serializer())
    )

    object GuildLeave
        : Route<Unit>(HttpMethod.Delete, "/users/@me/guilds/$GuildId", NoStrategy)

    object DMPost
        : Route<DiscordChannel>(HttpMethod.Post, "/users/@me/channels", DiscordChannel.serializer())

    object UserConnectionsGet
        : Route<List<Connection>>(HttpMethod.Get, "/users/@me/connections", ListSerializer(Connection.serializer()))

    object GuildPost
        : Route<DiscordGuild>(HttpMethod.Post, "/guilds", DiscordGuild.serializer())

    object GuildGet
        : Route<DiscordGuild>(HttpMethod.Get, "/guilds/$GuildId", DiscordGuild.serializer())

    object GuildPatch
        : Route<DiscordGuild>(HttpMethod.Patch, "/guilds/$GuildId", DiscordGuild.serializer())

    object GuildDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId", NoStrategy)

    object GuildChannelsGet
        : Route<List<DiscordChannel>>(
        HttpMethod.Get,
        "/guilds/$GuildId/channels",
        ListSerializer(DiscordChannel.serializer())
    )

    object GuildChannelsPost
        : Route<DiscordChannel>(HttpMethod.Post, "/guilds/$GuildId/channels", DiscordChannel.serializer())

    object GuildChannelsPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/channels", NoStrategy)

    object GuildMemberGet
        : Route<DiscordGuildMember>(HttpMethod.Get, "/guilds/$GuildId/members/$UserId", DiscordGuildMember.serializer())

    object GuildMembersGet
        : Route<List<DiscordGuildMember>>(
        HttpMethod.Get,
        "/guilds/$GuildId/members",
        ListSerializer(DiscordGuildMember.serializer())
    )

    @KordExperimental
    object GuildMembersSearchGet //https://github.com/discord/discord-api-docs/pull/1577
        : Route<List<DiscordGuildMember>>(
        HttpMethod.Get,
        "/guilds/$GuildId/members/search",
        ListSerializer(DiscordGuildMember.serializer())
    )

    object GuildMemberPut
        : Route<DiscordGuildMember?>(
        HttpMethod.Put,
        "/guilds/$GuildId/members/$UserId",
        DiscordGuildMember.serializer().optional
    )

    object GuildMemberPatch
        :
        Route<DiscordGuildMember>(HttpMethod.Patch, "/guilds/$GuildId/members/$UserId", DiscordGuildMember.serializer())

    object GuildCurrentUserNickPatch
        : Route<CurrentUserNicknameModifyResponse>(
        HttpMethod.Patch,
        "/guilds/$GuildId/members/@me/nick",
        CurrentUserNicknameModifyResponse.serializer()
    )

    object GuildMemberRolePut
        : Route<Unit>(HttpMethod.Put, "/guilds/$GuildId/members/$UserId/roles/$RoleId", NoStrategy)

    object GuildMemberRoleDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/members/$UserId/roles/$RoleId", NoStrategy)

    object GuildMemberDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/members/$UserId", NoStrategy)

    object GuildBansGet
        : Route<List<BanResponse>>(HttpMethod.Get, "/guilds/$GuildId/bans", ListSerializer(BanResponse.serializer()))

    object GuildBanGet
        : Route<BanResponse>(HttpMethod.Get, "/guilds/$GuildId/bans/$UserId", BanResponse.serializer())

    object GuildBanPut
        : Route<Unit>(HttpMethod.Put, "/guilds/$GuildId/bans/$UserId", NoStrategy)

    object GuildBanDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/bans/$UserId", NoStrategy)

    object GuildRolesGet
        : Route<List<DiscordRole>>(HttpMethod.Get, "/guilds/$GuildId/roles", ListSerializer(DiscordRole.serializer()))

    object GuildRolePost
        : Route<DiscordRole>(HttpMethod.Post, "/guilds/$GuildId/roles", DiscordRole.serializer())

    object GuildRolesPatch
        : Route<List<DiscordRole>>(HttpMethod.Patch, "/guilds/$GuildId/roles", ListSerializer(DiscordRole.serializer()))

    object GuildRolePatch
        : Route<DiscordRole>(HttpMethod.Patch, "/guilds/$GuildId/roles/$RoleId", DiscordRole.serializer())

    object GuildRoleDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/roles/$RoleId", NoStrategy)

    object GuildPruneCountGet
        : Route<GetPruneResponse>(HttpMethod.Get, "/guilds/$GuildId/prune", GetPruneResponse.serializer())

    object GuildPrunePost
        : Route<PruneResponse>(HttpMethod.Post, "/guilds/$GuildId/prune", PruneResponse.serializer())

    object GuildVoiceRegionsGet
        : Route<List<DiscordVoiceRegion>>(
        HttpMethod.Get,
        "/guilds/$GuildId/regions",
        ListSerializer(DiscordVoiceRegion.serializer())
    )

    object GuildInvitesGet
        : Route<List<DiscordInvite>>(
        HttpMethod.Get,
        "/guilds/$GuildId/invites",
        ListSerializer(DiscordInvite.serializer())
    )

    object GuildIntegrationGet
        : Route<List<DiscordIntegration>>(
        HttpMethod.Get,
        "/guilds/$GuildId/integrations",
        ListSerializer(DiscordIntegration.serializer())
    )

    object GuildIntegrationPost
        : Route<Unit>(HttpMethod.Post, "/guilds/$GuildId/integrations", NoStrategy)

    object GuildIntegrationPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/integrations/$IntegrationId", NoStrategy)

    object GuildIntegrationDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/integrations/$IntegrationId", NoStrategy)

    object GuildIntegrationSyncPost
        : Route<Unit>(HttpMethod.Post, "/guilds/$GuildId/integrations/$IntegrationId/sync", NoStrategy)

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("GuildWidgetGet"), DeprecationLevel.ERROR)
    object GuildEmbedGet
        : Route<Nothing>(HttpMethod.Get, "/guilds/$GuildId/embed", NothingSerializer)

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("GuildWidgetPatch"), DeprecationLevel.ERROR)
    object GuildEmbedPatch
        : Route<Nothing>(HttpMethod.Patch, "/guilds/$GuildId/embed", NothingSerializer)

    object GuildWidgetGet
        : Route<DiscordGuildWidget>(HttpMethod.Get, "/guilds/$GuildId/widget", DiscordGuildWidget.serializer())

    object GuildWidgetPatch
        : Route<DiscordGuildWidget>(HttpMethod.Patch, "/guilds/$GuildId/widget", DiscordGuildWidget.serializer())


    object GuildVanityInviteGet
        : Route<DiscordPartialInvite>(HttpMethod.Get, "/guilds/$GuildId/vanity-url", DiscordPartialInvite.serializer())

    object GuildWelcomeScreenGet
        : Route<DiscordWelcomeScreen>(
        HttpMethod.Get,
        "/guilds/${GuildId}/welcome-screen",
        DiscordWelcomeScreen.serializer()
    )

    object GuildWelcomeScreenPatch
        : Route<DiscordWelcomeScreen>(
        HttpMethod.Patch,
        "/guilds/${GuildId}/welcome-screen",
        DiscordWelcomeScreen.serializer()
    )


    object MessageCrosspost
        : Route<DiscordMessage>(
        HttpMethod.Post,
        "/channels/$ChannelId/messages/$MessageId/crosspost",
        DiscordMessage.serializer()
    )


    object NewsChannelFollow
        : Route<FollowedChannelResponse>(
        HttpMethod.Post,
        "/channels/$ChannelId/followers",
        FollowedChannelResponse.serializer()
    )


    /**
     * Returns the guild preview object for the given id, even if the user is not in the guild.
     *
     * This endpoint is only for Public guilds.
     */
    object GuildPreviewGet
        : Route<DiscordGuildPreview>(HttpMethod.Get, "/guilds/${GuildId}/preview", DiscordGuildPreview.serializer())

    object ChannelWebhooksGet
        : Route<List<DiscordWebhook>>(
        HttpMethod.Get,
        "/channels/$ChannelId/webhooks",
        ListSerializer(DiscordWebhook.serializer())
    )

    object GuildWebhooksGet
        : Route<List<DiscordWebhook>>(
        HttpMethod.Get,
        "/guilds/$GuildId/webhooks",
        ListSerializer(DiscordWebhook.serializer())
    )

    object WebhookGet
        : Route<DiscordWebhook>(HttpMethod.Get, "/webhooks/$WebhookId", DiscordWebhook.serializer())

    object WebhookPost
        : Route<DiscordWebhook>(HttpMethod.Post, "/channels/$ChannelId/webhooks", DiscordWebhook.serializer())

    object WebhookByTokenGet :
        Route<DiscordWebhook>(
            HttpMethod.Get,
            "/webhooks/$WebhookId/$WebhookToken",
            DiscordWebhook.serializer(),
            requiresAuthorizationHeader = false,
        )

    object WebhookPatch :
        Route<DiscordWebhook>(HttpMethod.Patch, "/webhooks/$WebhookId", DiscordWebhook.serializer())

    object WebhookByTokenPatch :
        Route<DiscordWebhook>(
            HttpMethod.Patch,
            "/webhooks/$WebhookId/$WebhookToken",
            DiscordWebhook.serializer(),
            requiresAuthorizationHeader = false,
        )

    object WebhookDelete
        : Route<Unit>(HttpMethod.Delete, "/webhooks/$WebhookId", NoStrategy)

    object WebhookByTokenDelete :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/$WebhookId/$WebhookToken",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    //TODO Make sure of the return of these routes below

    object ExecuteWebhookPost :
        Route<DiscordMessage?>(
            HttpMethod.Post,
            "/webhooks/$WebhookId/$WebhookToken",
            DiscordMessage.serializer().optional,
            requiresAuthorizationHeader = false,
        )

    object ExecuteSlackWebhookPost :
        Route<Unit>(
            HttpMethod.Post,
            "/webhooks/$WebhookId/$WebhookToken/slack",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    object ExecuteGithubWebhookPost :
        Route<Unit>(
            HttpMethod.Post,
            "/webhooks/$WebhookId/$WebhookToken/github",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    object GetWebhookMessage :
        Route<DiscordMessage>(
            HttpMethod.Get,
            "/webhooks/$WebhookId/$WebhookToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object EditWebhookMessage :
        Route<DiscordMessage>(
            HttpMethod.Patch,
            "/webhooks/$WebhookId/$WebhookToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object DeleteWebhookMessage :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/$WebhookId/$WebhookToken/messages/$MessageId",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    object VoiceRegionsGet
        : Route<List<DiscordVoiceRegion>>(
        HttpMethod.Get,
        "/voice/regions",
        ListSerializer(DiscordVoiceRegion.serializer())
    )

    object CurrentApplicationInfo
        :
        Route<ApplicationInfoResponse>(HttpMethod.Get, "/oauth2/applications/@me", ApplicationInfoResponse.serializer())

    object TemplateGet
        : Route<DiscordTemplate>(HttpMethod.Get, "guilds/templates/${TemplateCode}", DiscordTemplate.serializer())

    object GuildFromTemplatePost
        : Route<DiscordGuild>(HttpMethod.Post, "guilds/templates/${TemplateCode}", DiscordGuild.serializer())

    object GuildTemplatesGet
        : Route<List<DiscordTemplate>>(
        HttpMethod.Get,
        "/guilds/${GuildId}/templates",
        ListSerializer(DiscordTemplate.serializer())
    )

    object GuildTemplatePost
        : Route<DiscordTemplate>(HttpMethod.Post, "/guilds/${GuildId}/templates", DiscordTemplate.serializer())

    object TemplateSyncPut
        : Route<DiscordTemplate>(
        HttpMethod.Put,
        "/guilds/${GuildId}/templates/${TemplateCode}",
        DiscordTemplate.serializer()
    )

    object TemplatePatch
        : Route<DiscordTemplate>(
        HttpMethod.Patch,
        "/guilds/${GuildId}/templates/${TemplateCode}",
        DiscordTemplate.serializer()
    )

    object TemplateDelete
        : Route<DiscordTemplate>(
        HttpMethod.Delete,
        "/guilds/${GuildId}/templates/${TemplateCode}",
        DiscordTemplate.serializer()
    )


    object GlobalApplicationCommandsGet
        : Route<List<DiscordApplicationCommand>>(
        HttpMethod.Get,
        "/applications/${ApplicationId}/commands",
        ListSerializer(DiscordApplicationCommand.serializer())
    )


    object GlobalApplicationCommandCreate : Route<DiscordApplicationCommand>(
        HttpMethod.Post,
        "/applications/${ApplicationId}/commands",
        DiscordApplicationCommand.serializer()
    )


    object GlobalApplicationCommandsCreate : Route<List<DiscordApplicationCommand>>(
        HttpMethod.Put,
        "/applications/${ApplicationId}/commands",
        ListSerializer(DiscordApplicationCommand.serializer())
    )


    object GlobalApplicationCommandModify : Route<DiscordApplicationCommand>(
        HttpMethod.Patch,
        "/applications/${ApplicationId}/commands/${CommandId}",
        DiscordApplicationCommand.serializer()
    )


    object GlobalApplicationCommandGet
        : Route<DiscordApplicationCommand>(
        HttpMethod.Get,
        "/applications/${ApplicationId}/commands/${CommandId}",
        DiscordApplicationCommand.serializer()
    )

    object GlobalApplicationCommandDelete
        : Route<Unit>(
        HttpMethod.Delete, "/applications/${ApplicationId}/commands/${CommandId}", NoStrategy
    )


    object GuildApplicationCommandsGet
        : Route<List<DiscordApplicationCommand>>(
        HttpMethod.Get,
        "/applications/${ApplicationId}/guilds/${GuildId}/commands",
        ListSerializer(DiscordApplicationCommand.serializer())
    )


    object GuildApplicationCommandCreate : Route<DiscordApplicationCommand>(
        HttpMethod.Post,
        "/applications/${ApplicationId}/guilds/${GuildId}/commands",
        DiscordApplicationCommand.serializer()
    )


    object GuildApplicationCommandsCreate : Route<List<DiscordApplicationCommand>>(
        HttpMethod.Put,
        "/applications/${ApplicationId}/guilds/${GuildId}/commands",
        ListSerializer(DiscordApplicationCommand.serializer())
    )


    object GuildApplicationCommandModify
        : Route<DiscordApplicationCommand>(
        HttpMethod.Patch,
        "/applications/${ApplicationId}/guilds/${GuildId}/commands/${CommandId}",
        DiscordApplicationCommand.serializer()
    )


    object GuildApplicationCommandGet
        : Route<DiscordApplicationCommand>(
        HttpMethod.Get,
        "/applications/${ApplicationId}/guilds/${GuildId}/commands/${CommandId}",
        DiscordApplicationCommand.serializer()
    )

    object GuildApplicationCommandDelete
        : Route<Unit>(
        HttpMethod.Delete,
        "/applications/${ApplicationId}/guilds/{guild.id}/commands/${CommandId}",
        NoStrategy
    )

    object GuildScheduledEventsGet : Route<List<DiscordGuildScheduledEvent>>(
        HttpMethod.Get,
        "/guilds/$GuildId/events",
        ListSerializer(DiscordGuildScheduledEvent.serializer())
    )

    object GuildScheduledEventGet : Route<DiscordGuildScheduledEvent>(
        HttpMethod.Get,
        "/guilds/$GuildId/events/$ScheduledEventId",
        DiscordGuildScheduledEvent.serializer()
    )

    object GuildScheduledEventUsersGet : Route<GuildScheduledEventUsersResponse>(
        HttpMethod.Get,
        "/guilds/$GuildId/events/$ScheduledEventId/users",
        GuildScheduledEventUsersResponse.serializer()
    )

    object GuildScheduledEventPatch : Route<DiscordGuildScheduledEvent>(
        HttpMethod.Patch,
        "/guilds/$GuildId/events/$ScheduledEventId",
        DiscordGuildScheduledEvent.serializer()
    )

    object GuildScheduledEventDelete : Route<Unit>(
        HttpMethod.Delete,
        "/guilds/$GuildId/events/$ScheduledEventId",
        NoStrategy
    )

    object GuildScheduledEventsPost : Route<DiscordGuildScheduledEvent>(
        HttpMethod.Post,
        "/guilds/$GuildId/events",
        DiscordGuildScheduledEvent.serializer()
    )

    object InteractionResponseCreate :
        Route<Unit>(
            HttpMethod.Post,
            "/interactions/${InteractionId}/${InteractionToken}/callback",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    object OriginalInteractionResponseGet :
        Route<DiscordMessage>(
            HttpMethod.Get,
            "/webhooks/${ApplicationId}/${InteractionToken}/messages/@original",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object OriginalInteractionResponseModify :
        Route<DiscordMessage>(
            HttpMethod.Patch,
            "/webhooks/${ApplicationId}/${InteractionToken}/messages/@original",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object OriginalInteractionResponseDelete :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/${ApplicationId}/${InteractionToken}/messages/@original",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )


    object GuildApplicationCommandPermissionsGet
        : Route<List<DiscordGuildApplicationCommandPermissions>>(
        HttpMethod.Get,
        "/applications/${ApplicationId}/guilds/$GuildId/commands/permissions",
        ListSerializer(DiscordGuildApplicationCommandPermissions.serializer())
    )


    object ApplicationCommandPermissionsGet
        : Route<DiscordGuildApplicationCommandPermissions>(
        HttpMethod.Get,
        "/applications/${ApplicationId}/guilds/$GuildId/commands/$CommandId/permissions",
        DiscordGuildApplicationCommandPermissions.serializer()
    )


    object ApplicationCommandPermissionsPut
        : Route<DiscordGuildApplicationCommandPermissions>(
        HttpMethod.Put,
        "/applications/$ApplicationId/guilds/$GuildId/commands/$CommandId/permissions",
        DiscordGuildApplicationCommandPermissions.serializer()
    )


    object ApplicationCommandPermissionsBatchPut
        : Route<List<DiscordGuildApplicationCommandPermissions>>(
        HttpMethod.Put,
        "/applications/$ApplicationId/guilds/$GuildId/commands/permissions",
        serializer()
    )

    object FollowupMessageCreate :
        Route<DiscordMessage>(
            HttpMethod.Post,
            "/webhooks/${ApplicationId}/${InteractionToken}",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object FollowupMessageGet :
        Route<DiscordMessage>(
            HttpMethod.Get,
            "/webhooks/$ApplicationId/$InteractionToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object FollowupMessageModify :
        Route<DiscordMessage>(
            HttpMethod.Patch,
            "/webhooks/${ApplicationId}/${InteractionToken}/messages/${MessageId}",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    object FollowupMessageDelete :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/${ApplicationId}/${InteractionToken}/messages/${MessageId}",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    object SelfVoiceStatePatch :
        Route<Unit>(HttpMethod.Patch, "/guilds/${GuildId}/voice-states/@me", NoStrategy)


    object OthersVoiceStatePatch :
        Route<Unit>(HttpMethod.Patch, "/guilds/${GuildId}/voice-states/${UserId}", NoStrategy)

    object StageInstanceGet :
        Route<DiscordStageInstance>(HttpMethod.Get, "/stage-instances/$ChannelId", DiscordStageInstance.serializer())

    object StageInstancePost :
        Route<DiscordStageInstance>(HttpMethod.Post, "/stage-instances", DiscordStageInstance.serializer())

    object StageInstancePatch :
        Route<DiscordStageInstance>(HttpMethod.Patch, "/stage-instances/$ChannelId", DiscordStageInstance.serializer())

    object StageInstanceDelete :
        Route<Unit>(HttpMethod.Delete, "/stage-instances/$ChannelId", NoStrategy)

    object StartPublicThreadWithMessagePost :
        Route<DiscordChannel>(
            HttpMethod.Post,
            "/channels/${ChannelId}/messages/${MessageId}/threads",
            DiscordChannel.serializer()
        )


    object StartThreadPost :
        Route<DiscordChannel>(HttpMethod.Post, "/channels/${ChannelId}/threads", DiscordChannel.serializer())

    object JoinThreadPut :
        Route<Unit>(HttpMethod.Put, "/channels/${ChannelId}/thread-members/@me", NoStrategy)

    object AddThreadMemberPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/thread-members/${UserId}", NoStrategy)

    object LeaveThreadDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/${ChannelId}/thread-members/@me", NoStrategy)

    object RemoveUserFromThreadDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/${ChannelId}/thread-members/${UserId}", NoStrategy)

    object ThreadMembersGet :
        Route<List<DiscordThreadMember>>(
            HttpMethod.Get,
            "/channels/${ChannelId}/thread-members",
            ListSerializer(DiscordThreadMember.serializer())
        )


    object PrivateThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/${ChannelId}/threads/private",
            ListThreadsResponse.serializer()
        )


    object PrivateArchivedThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/${ChannelId}/threads/archived/private",
            ListThreadsResponse.serializer()
        )

    object PublicArchivedThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/${ChannelId}/threads/archived/public",
            ListThreadsResponse.serializer()
        )

    object JoinedPrivateArchivedThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/$ChannelId/users/@me/threads/archived/private",
            ListThreadsResponse.serializer()
        )

    object ActiveThreadsGet : Route<ListThreadsResponse>(
        HttpMethod.Get,
        "/guilds/${GuildId}/threads/active",
        ListThreadsResponse.serializer()
    )

    object StickerGet : Route<DiscordMessageSticker>(
        HttpMethod.Get,
        "/stickers/${StickerId}",
        DiscordMessageSticker.serializer()
    )

    object NitroStickerPacks : Route<List<DiscordStickerPack>>(
        HttpMethod.Get,
        "/sticker-packs",
        ListSerializer(DiscordStickerPack.serializer())
    )

    object GuildStickersGet : Route<List<DiscordMessageSticker>>(
        HttpMethod.Get,
        "/guilds/${GuildId}/stickers",
        ListSerializer(DiscordMessageSticker.serializer())
    )

    object GuildStickerGet : Route<DiscordMessageSticker>(
        HttpMethod.Get,
        "/guilds/${GuildId}/stickers/${StickerId}",
        DiscordMessageSticker.serializer()
    )


    object GuildStickerDelete : Route<Unit>(
        HttpMethod.Delete,
        "/guilds/${GuildId}/stickers/${StickerId}",
        NoStrategy
    )

    object GuildStickerPost: Route<DiscordMessageSticker>(
        HttpMethod.Post,
        "/guilds/${GuildId}/stickers",
        DiscordMessageSticker.serializer()
    )


    object GuildStickerPatch: Route<DiscordMessageSticker>(
        HttpMethod.Patch,
        "/guilds/${GuildId}/stickers/${StickerId}",
        DiscordMessageSticker.serializer()
    )


    companion object {
        val baseUrl = "https://discord.com/api/$restVersion"
    }

    open class Key(val identifier: String, val isMajor: Boolean = false) {
        override fun toString(): String = identifier
    }

    object GuildId : Key("{guild.id}", true)
    object ChannelId : Key("{channel.id}", true)
    object MessageId : Key("{message.id}")
    object Emoji : Key("{emoji}")
    object UserId : Key("{user.id}")
    object OverwriteId : Key("{overwrite.id}")
    object EmojiId : Key("{emoji.id}")
    object InviteCode : Key("{invite.code}")
    object RoleId : Key("{role.id}")
    object IntegrationId : Key("{integration.id}")
    object WebhookId : Key("{webhook.id}", true)
    object WebhookToken : Key("{webhook.token}")
    object TemplateCode : Key("{template.code}")
    object ApplicationId : Key("{application.id}", true)
    object CommandId : Key("{command.id}", true)
    object InteractionId : Key("{interaction.id}", true)
    object InteractionToken : Key("{interaction.token}", true)
    object ScheduledEventId : Key("{event.id}", true)
    object StickerId : Key("{sticker.id}")

}

internal object NoStrategy : DeserializationStrategy<Unit> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("NoStrategy", StructureKind.OBJECT) {}

    override fun deserialize(decoder: Decoder) {}

}
