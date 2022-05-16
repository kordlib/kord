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

internal const val REST_VERSION_PROPERTY_NAME = "dev.kord.rest.version"
internal val restVersion get() = System.getenv(REST_VERSION_PROPERTY_NAME) ?: "v10"

public sealed interface ResponseMapper<T> {
    public fun deserialize(json: Json, body: String): T
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

internal object NoStrategy : DeserializationStrategy<Unit> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("NoStrategy", StructureKind.OBJECT) {}

    override fun deserialize(decoder: Decoder) {}
}

public sealed class Route<T>(
    public val method: HttpMethod,
    public val path: String,
    public val mapper: ResponseMapper<T>,
    public val requiresAuthorizationHeader: Boolean = true,
) {

    public companion object {
        public val baseUrl: String = "https://discord.com/api/$restVersion"
    }


    public open class Key(public val identifier: String, public val isMajor: Boolean = false) {
        override fun toString(): String = identifier
    }

    public object GuildId : Key("{guild.id}", true)
    public object ChannelId : Key("{channel.id}", true)
    public object MessageId : Key("{message.id}")
    public object Emoji : Key("{emoji}")
    public object UserId : Key("{user.id}")
    public object OverwriteId : Key("{overwrite.id}")
    public object EmojiId : Key("{emoji.id}")
    public object InviteCode : Key("{invite.code}")
    public object RoleId : Key("{role.id}")
    public object IntegrationId : Key("{integration.id}")
    public object WebhookId : Key("{webhook.id}", true)
    public object WebhookToken : Key("{webhook.token}")
    public object TemplateCode : Key("{template.code}")
    public object ApplicationId : Key("{application.id}", true)
    public object CommandId : Key("{command.id}", true)
    public object InteractionId : Key("{interaction.id}", true)
    public object InteractionToken : Key("{interaction.token}", true)
    public object ScheduledEventId : Key("{event.id}", true)
    public object StickerId : Key("{sticker.id}")


    protected constructor(
        method: HttpMethod,
        path: String,
        strategy: DeserializationStrategy<T>,
        requiresAuthorizationHeader: Boolean = true,
    ) : this(method, path, ValueJsonMapper(strategy), requiresAuthorizationHeader)

    override fun toString(): String = "Route(method:${method.value},path:$path,mapper:$mapper)"


    /*
     * Gateway:
     * https://discord.com/developers/docs/topics/gateway
     */

    public object GatewayGet :
        Route<GatewayResponse>(
            HttpMethod.Get,
            "/gateway",
            GatewayResponse.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object GatewayBotGet :
        Route<BotGatewayResponse>(HttpMethod.Get, "/gateway/bot", BotGatewayResponse.serializer())


    /*
     * Audit Log:
     * https://discord.com/developers/docs/resources/audit-log
     */

    public object AuditLogGet :
        Route<DiscordAuditLog>(HttpMethod.Get, "/guilds/$GuildId/audit-logs", DiscordAuditLog.serializer())


    /*
     * Channel:
     * https://discord.com/developers/docs/resources/channel
     */

    public object ChannelGet :
        Route<DiscordChannel>(HttpMethod.Get, "/channels/$ChannelId", DiscordChannel.serializer())

    public object ChannelPut : // TODO does this route still/even exist?
        Route<DiscordChannel>(HttpMethod.Put, "/channels/$ChannelId", DiscordChannel.serializer())

    public object ChannelPatch :
        Route<DiscordChannel>(HttpMethod.Patch, "/channels/$ChannelId", DiscordChannel.serializer())

    public object ChannelDelete :
        Route<DiscordChannel>(HttpMethod.Delete, "/channels/$ChannelId", DiscordChannel.serializer())

    public object MessagePost :
        Route<DiscordMessage>(HttpMethod.Post, "/channels/$ChannelId/messages", DiscordMessage.serializer())

    public object MessageGet :
        Route<DiscordMessage>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId", DiscordMessage.serializer())

    public object MessagesGet :
        Route<List<DiscordMessage>>(
            HttpMethod.Get,
            "/channels/$ChannelId/messages",
            ListSerializer(DiscordMessage.serializer())
        )

    public object MessageCrosspost :
        Route<DiscordMessage>(
            HttpMethod.Post,
            "/channels/$ChannelId/messages/$MessageId/crosspost",
            DiscordMessage.serializer()
        )

    public object PinsGet :
        Route<List<DiscordMessage>>(
            HttpMethod.Get,
            "/channels/$ChannelId/pins",
            ListSerializer(DiscordMessage.serializer())
        )

    public object InvitesGet :
        Route<List<DiscordInviteWithMetadata>>(
            HttpMethod.Get,
            "/channels/$ChannelId/invites",
            ListSerializer(DiscordInviteWithMetadata.serializer())
        )

    public object InvitePost :
        Route<DiscordInviteWithMetadata>(
            HttpMethod.Post,
            "/channels/$ChannelId/invites",
            DiscordInviteWithMetadata.serializer(),
        )

    public object ReactionPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    public object OwnReactionDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    public object ReactionDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/$UserId", NoStrategy)

    public object DeleteAllReactionsForEmoji :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji", NoStrategy)

    public object MessageDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId", NoStrategy)

    public object BulkMessageDeletePost :
        Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/messages/bulk-delete", NoStrategy)

    public object PinDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/pins/$MessageId", NoStrategy)

    public object PinPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/pins/$MessageId", NoStrategy)

    public object AllReactionsDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions", NoStrategy)

    public object ChannelPermissionDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/permissions/$OverwriteId", NoStrategy)

    public object ChannelPermissionPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/permissions/$OverwriteId", NoStrategy)

    public object ReactionsGet :
        Route<List<DiscordUser>>(
            HttpMethod.Get,
            "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji",
            ListSerializer(DiscordUser.serializer())
        )

    public object NewsChannelFollow :
        Route<FollowedChannelResponse>(
            HttpMethod.Post,
            "/channels/$ChannelId/followers",
            FollowedChannelResponse.serializer()
        )

    public object TypingIndicatorPost :
        Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/typing", NoStrategy)

    public object GroupDMUserDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    public object GroupDMUserPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    public object EditMessagePatch :
        Route<DiscordMessage>(HttpMethod.Patch, "/channels/$ChannelId/messages/$MessageId", DiscordMessage.serializer())

    public object StartPublicThreadWithMessagePost :
        Route<DiscordChannel>(
            HttpMethod.Post,
            "/channels/$ChannelId/messages/$MessageId/threads",
            DiscordChannel.serializer()
        )

    public object StartThreadPost :
        Route<DiscordChannel>(HttpMethod.Post, "/channels/$ChannelId/threads", DiscordChannel.serializer())

    public object JoinThreadPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/thread-members/@me", NoStrategy)

    public object AddThreadMemberPut :
        Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/thread-members/$UserId", NoStrategy)

    public object LeaveThreadDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/thread-members/@me", NoStrategy)

    public object RemoveUserFromThreadDelete :
        Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/thread-members/$UserId", NoStrategy)

    public object ThreadMembersGet :
        Route<List<DiscordThreadMember>>(
            HttpMethod.Get,
            "/channels/$ChannelId/thread-members",
            ListSerializer(DiscordThreadMember.serializer())
        )

    public object PrivateThreadsGet : // TODO does this route still/even exist?
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/$ChannelId/threads/private",
            ListThreadsResponse.serializer()
        )

    public object PrivateArchivedThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/$ChannelId/threads/archived/private",
            ListThreadsResponse.serializer()
        )

    public object PublicArchivedThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/$ChannelId/threads/archived/public",
            ListThreadsResponse.serializer()
        )

    public object JoinedPrivateArchivedThreadsGet :
        Route<ListThreadsResponse>(
            HttpMethod.Get,
            "/channels/$ChannelId/users/@me/threads/archived/private",
            ListThreadsResponse.serializer()
        )


    /*
     * Emoji:
     * https://discord.com/developers/docs/resources/emoji
     */

    public object GuildEmojiGet :
        Route<DiscordEmoji>(HttpMethod.Get, "/guilds/$GuildId/emojis/$EmojiId", DiscordEmoji.serializer())

    public object GuildEmojisGet :
        Route<List<DiscordEmoji>>(HttpMethod.Get, "/guilds/$GuildId/emojis", ListSerializer(DiscordEmoji.serializer()))

    public object GuildEmojiDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/emojis/$EmojiId", NoStrategy)

    public object GuildEmojiPost :
        Route<DiscordEmoji>(HttpMethod.Post, "/guilds/$GuildId/emojis", DiscordEmoji.serializer())

    public object GuildEmojiPatch :
        Route<DiscordEmoji>(HttpMethod.Patch, "/guilds/$GuildId/emojis/$EmojiId", DiscordEmoji.serializer())


    /*
     * Invite:
     * https://discord.com/developers/docs/resources/invite
     */

    public object InviteGet :
        Route<DiscordInvite>(HttpMethod.Get, "/invites/$InviteCode", DiscordInvite.serializer())

    public object InviteDelete :
        Route<DiscordInvite>(HttpMethod.Delete, "/invites/$InviteCode", DiscordInvite.serializer())


    /*
     * User:
     * https://discord.com/developers/docs/resources/user
     */

    public object CurrentUserGet :
        Route<DiscordUser>(HttpMethod.Get, "/users/@me", DiscordUser.serializer())

    public object CurrentUserPatch :
        Route<DiscordUser>(HttpMethod.Patch, "/users/@me", DiscordUser.serializer())

    public object UserGet :
        Route<DiscordUser>(HttpMethod.Get, "/users/$UserId", DiscordUser.serializer())

    public object CurrentUsersGuildsGet :
        Route<List<DiscordPartialGuild>>(
            HttpMethod.Get,
            "/users/@me/guilds",
            ListSerializer(DiscordPartialGuild.serializer())
        )

    public object GuildLeave :
        Route<Unit>(HttpMethod.Delete, "/users/@me/guilds/$GuildId", NoStrategy)

    public object DMPost :
        Route<DiscordChannel>(HttpMethod.Post, "/users/@me/channels", DiscordChannel.serializer())

    public object UserConnectionsGet :
        Route<List<Connection>>(HttpMethod.Get, "/users/@me/connections", ListSerializer(Connection.serializer()))


    /*
     * Guild:
     * https://discord.com/developers/docs/resources/guild
     */

    public object GuildPost :
        Route<DiscordGuild>(HttpMethod.Post, "/guilds", DiscordGuild.serializer())

    public object GuildGet :
        Route<DiscordGuild>(HttpMethod.Get, "/guilds/$GuildId", DiscordGuild.serializer())

    public object GuildPatch :
        Route<DiscordGuild>(HttpMethod.Patch, "/guilds/$GuildId", DiscordGuild.serializer())

    public object GuildDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId", NoStrategy)

    public object GuildChannelsGet :
        Route<List<DiscordChannel>>(
            HttpMethod.Get,
            "/guilds/$GuildId/channels",
            ListSerializer(DiscordChannel.serializer())
        )

    public object GuildChannelsPost :
        Route<DiscordChannel>(HttpMethod.Post, "/guilds/$GuildId/channels", DiscordChannel.serializer())

    public object GuildChannelsPatch :
        Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/channels", NoStrategy)

    public object ActiveThreadsGet :
        Route<ListThreadsResponse>(HttpMethod.Get, "/guilds/$GuildId/threads/active", ListThreadsResponse.serializer())

    public object GuildMemberGet :
        Route<DiscordGuildMember>(HttpMethod.Get, "/guilds/$GuildId/members/$UserId", DiscordGuildMember.serializer())

    public object GuildMembersGet :
        Route<List<DiscordGuildMember>>(
            HttpMethod.Get,
            "/guilds/$GuildId/members",
            ListSerializer(DiscordGuildMember.serializer())
        )

    @KordExperimental
    public object GuildMembersSearchGet : // https://github.com/discord/discord-api-docs/pull/1577
        Route<List<DiscordGuildMember>>(
            HttpMethod.Get,
            "/guilds/$GuildId/members/search",
            ListSerializer(DiscordGuildMember.serializer())
        )

    public object GuildMemberPut :
        Route<DiscordGuildMember?>(
            HttpMethod.Put,
            "/guilds/$GuildId/members/$UserId",
            DiscordGuildMember.serializer().optional
        )

    public object GuildMemberPatch :
        Route<DiscordGuildMember>(HttpMethod.Patch, "/guilds/$GuildId/members/$UserId", DiscordGuildMember.serializer())

    public object GuildCurrentUserNickPatch :
        Route<CurrentUserNicknameModifyResponse>(
            HttpMethod.Patch,
            "/guilds/$GuildId/members/@me/nick",
            CurrentUserNicknameModifyResponse.serializer()
        )

    public object GuildMemberRolePut :
        Route<Unit>(HttpMethod.Put, "/guilds/$GuildId/members/$UserId/roles/$RoleId", NoStrategy)

    public object GuildMemberRoleDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/members/$UserId/roles/$RoleId", NoStrategy)

    public object GuildMemberDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/members/$UserId", NoStrategy)

    public object GuildBansGet :
        Route<List<BanResponse>>(HttpMethod.Get, "/guilds/$GuildId/bans", ListSerializer(BanResponse.serializer()))

    public object GuildBanGet :
        Route<BanResponse>(HttpMethod.Get, "/guilds/$GuildId/bans/$UserId", BanResponse.serializer())

    public object GuildBanPut :
        Route<Unit>(HttpMethod.Put, "/guilds/$GuildId/bans/$UserId", NoStrategy)

    public object GuildBanDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/bans/$UserId", NoStrategy)

    public object GuildRolesGet :
        Route<List<DiscordRole>>(HttpMethod.Get, "/guilds/$GuildId/roles", ListSerializer(DiscordRole.serializer()))

    public object GuildRolePost :
        Route<DiscordRole>(HttpMethod.Post, "/guilds/$GuildId/roles", DiscordRole.serializer())

    public object GuildRolesPatch :
        Route<List<DiscordRole>>(HttpMethod.Patch, "/guilds/$GuildId/roles", ListSerializer(DiscordRole.serializer()))

    public object GuildRolePatch :
        Route<DiscordRole>(HttpMethod.Patch, "/guilds/$GuildId/roles/$RoleId", DiscordRole.serializer())

    public object GuildRoleDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/roles/$RoleId", NoStrategy)

    public object GuildPruneCountGet :
        Route<GetPruneResponse>(HttpMethod.Get, "/guilds/$GuildId/prune", GetPruneResponse.serializer())

    public object GuildPrunePost :
        Route<PruneResponse>(HttpMethod.Post, "/guilds/$GuildId/prune", PruneResponse.serializer())

    public object GuildVoiceRegionsGet :
        Route<List<DiscordVoiceRegion>>(
            HttpMethod.Get,
            "/guilds/$GuildId/regions",
            ListSerializer(DiscordVoiceRegion.serializer())
        )

    public object GuildInvitesGet :
        Route<List<DiscordInviteWithMetadata>>(
            HttpMethod.Get,
            "/guilds/$GuildId/invites",
            ListSerializer(DiscordInviteWithMetadata.serializer()),
        )

    public object GuildIntegrationGet :
        Route<List<DiscordIntegration>>(
            HttpMethod.Get,
            "/guilds/$GuildId/integrations",
            ListSerializer(DiscordIntegration.serializer())
        )

    public object GuildIntegrationPost : // TODO does this endpoint still/even exist?
        Route<Unit>(HttpMethod.Post, "/guilds/$GuildId/integrations", NoStrategy)

    public object GuildIntegrationPatch : // TODO does this endpoint still/even exist?
        Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/integrations/$IntegrationId", NoStrategy)

    public object GuildIntegrationDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/integrations/$IntegrationId", NoStrategy)

    public object GuildIntegrationSyncPost : // TODO does this endpoint still/even exist?
        Route<Unit>(HttpMethod.Post, "/guilds/$GuildId/integrations/$IntegrationId/sync", NoStrategy)

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("GuildWidgetGet"), DeprecationLevel.ERROR)
    public object GuildEmbedGet :
        Route<Nothing>(HttpMethod.Get, "/guilds/$GuildId/embed", NothingSerializer)

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("GuildWidgetPatch"), DeprecationLevel.ERROR)
    public object GuildEmbedPatch :
        Route<Nothing>(HttpMethod.Patch, "/guilds/$GuildId/embed", NothingSerializer)

    public object GuildWidgetGet :
        Route<DiscordGuildWidget>(HttpMethod.Get, "/guilds/$GuildId/widget", DiscordGuildWidget.serializer())

    public object GuildWidgetPatch :
        Route<DiscordGuildWidget>(HttpMethod.Patch, "/guilds/$GuildId/widget", DiscordGuildWidget.serializer())

    public object GuildVanityInviteGet :
        Route<DiscordPartialInvite>(HttpMethod.Get, "/guilds/$GuildId/vanity-url", DiscordPartialInvite.serializer())

    public object GuildWelcomeScreenGet :
        Route<DiscordWelcomeScreen>(
            HttpMethod.Get,
            "/guilds/$GuildId/welcome-screen",
            DiscordWelcomeScreen.serializer()
        )

    public object GuildWelcomeScreenPatch :
        Route<DiscordWelcomeScreen>(
            HttpMethod.Patch,
            "/guilds/$GuildId/welcome-screen",
            DiscordWelcomeScreen.serializer()
        )

    public object GuildPreviewGet :
        Route<DiscordGuildPreview>(HttpMethod.Get, "/guilds/$GuildId/preview", DiscordGuildPreview.serializer())

    public object SelfVoiceStatePatch :
        Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/voice-states/@me", NoStrategy)

    public object OthersVoiceStatePatch :
        Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/voice-states/$UserId", NoStrategy)


    /*
     * Webhook:
     * https://discord.com/developers/docs/resources/webhook
     */

    public object ChannelWebhooksGet :
        Route<List<DiscordWebhook>>(
            HttpMethod.Get,
            "/channels/$ChannelId/webhooks",
            ListSerializer(DiscordWebhook.serializer())
        )

    public object GuildWebhooksGet :
        Route<List<DiscordWebhook>>(
            HttpMethod.Get,
            "/guilds/$GuildId/webhooks",
            ListSerializer(DiscordWebhook.serializer())
        )

    public object WebhookGet :
        Route<DiscordWebhook>(HttpMethod.Get, "/webhooks/$WebhookId", DiscordWebhook.serializer())

    public object WebhookPost :
        Route<DiscordWebhook>(HttpMethod.Post, "/channels/$ChannelId/webhooks", DiscordWebhook.serializer())

    public object WebhookByTokenGet :
        Route<DiscordWebhook>(
            HttpMethod.Get,
            "/webhooks/$WebhookId/$WebhookToken",
            DiscordWebhook.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object WebhookPatch :
        Route<DiscordWebhook>(HttpMethod.Patch, "/webhooks/$WebhookId", DiscordWebhook.serializer())

    public object WebhookByTokenPatch :
        Route<DiscordWebhook>(
            HttpMethod.Patch,
            "/webhooks/$WebhookId/$WebhookToken",
            DiscordWebhook.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object WebhookDelete :
        Route<Unit>(HttpMethod.Delete, "/webhooks/$WebhookId", NoStrategy)

    public object WebhookByTokenDelete :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/$WebhookId/$WebhookToken",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    public object ExecuteWebhookPost :
        Route<DiscordMessage?>(
            HttpMethod.Post,
            "/webhooks/$WebhookId/$WebhookToken",
            DiscordMessage.serializer().optional,
            requiresAuthorizationHeader = false,
        )

    public object ExecuteSlackWebhookPost :
        Route<Unit>(
            HttpMethod.Post,
            "/webhooks/$WebhookId/$WebhookToken/slack",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    public object ExecuteGithubWebhookPost :
        Route<Unit>(
            HttpMethod.Post,
            "/webhooks/$WebhookId/$WebhookToken/github",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    public object GetWebhookMessage :
        Route<DiscordMessage>(
            HttpMethod.Get,
            "/webhooks/$WebhookId/$WebhookToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object EditWebhookMessage :
        Route<DiscordMessage>(
            HttpMethod.Patch,
            "/webhooks/$WebhookId/$WebhookToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object DeleteWebhookMessage :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/$WebhookId/$WebhookToken/messages/$MessageId",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )


    /*
     * Voice:
     * https://discord.com/developers/docs/resources/voice
     */

    public object VoiceRegionsGet :
        Route<List<DiscordVoiceRegion>>(
            HttpMethod.Get,
            "/voice/regions",
            ListSerializer(DiscordVoiceRegion.serializer())
        )


    /*
     * OAuth2:
     * https://discord.com/developers/docs/topics/oauth2
     */

    public object CurrentApplicationInfo :
        Route<DiscordApplication>(HttpMethod.Get, "/oauth2/applications/@me", DiscordApplication.serializer())


    /*
     * Guild Template:
     * https://discord.com/developers/docs/resources/guild-template
     */

    public object TemplateGet :
        Route<DiscordTemplate>(HttpMethod.Get, "guilds/templates/$TemplateCode", DiscordTemplate.serializer())

    public object GuildFromTemplatePost :
        Route<DiscordGuild>(HttpMethod.Post, "guilds/templates/$TemplateCode", DiscordGuild.serializer())

    public object GuildTemplatesGet :
        Route<List<DiscordTemplate>>(
            HttpMethod.Get,
            "/guilds/$GuildId/templates",
            ListSerializer(DiscordTemplate.serializer())
        )

    public object GuildTemplatePost :
        Route<DiscordTemplate>(HttpMethod.Post, "/guilds/$GuildId/templates", DiscordTemplate.serializer())

    public object TemplateSyncPut :
        Route<DiscordTemplate>(HttpMethod.Put, "/guilds/$GuildId/templates/$TemplateCode", DiscordTemplate.serializer())

    public object TemplatePatch :
        Route<DiscordTemplate>(
            HttpMethod.Patch,
            "/guilds/$GuildId/templates/$TemplateCode",
            DiscordTemplate.serializer()
        )

    public object TemplateDelete :
        Route<DiscordTemplate>(
            HttpMethod.Delete,
            "/guilds/$GuildId/templates/$TemplateCode",
            DiscordTemplate.serializer()
        )


    /*
     * Interactions - Application Commands:
     * https://discord.com/developers/docs/interactions/application-commands
     */

    public object GlobalApplicationCommandsGet :
        Route<List<DiscordApplicationCommand>>(
            HttpMethod.Get,
            "/applications/$ApplicationId/commands",
            ListSerializer(DiscordApplicationCommand.serializer())
        )

    public object GlobalApplicationCommandCreate :
        Route<DiscordApplicationCommand>(
            HttpMethod.Post,
            "/applications/$ApplicationId/commands",
            DiscordApplicationCommand.serializer()
        )

    public object GlobalApplicationCommandsCreate :
        Route<List<DiscordApplicationCommand>>(
            HttpMethod.Put,
            "/applications/$ApplicationId/commands",
            ListSerializer(DiscordApplicationCommand.serializer())
        )

    public object GlobalApplicationCommandModify :
        Route<DiscordApplicationCommand>(
            HttpMethod.Patch,
            "/applications/$ApplicationId/commands/$CommandId",
            DiscordApplicationCommand.serializer()
        )

    public object GlobalApplicationCommandGet :
        Route<DiscordApplicationCommand>(
            HttpMethod.Get,
            "/applications/$ApplicationId/commands/$CommandId",
            DiscordApplicationCommand.serializer()
        )

    public object GlobalApplicationCommandDelete :
        Route<Unit>(HttpMethod.Delete, "/applications/$ApplicationId/commands/$CommandId", NoStrategy)

    public object GuildApplicationCommandsGet :
        Route<List<DiscordApplicationCommand>>(
            HttpMethod.Get,
            "/applications/$ApplicationId/guilds/$GuildId/commands",
            ListSerializer(DiscordApplicationCommand.serializer())
        )

    public object GuildApplicationCommandCreate :
        Route<DiscordApplicationCommand>(
            HttpMethod.Post,
            "/applications/$ApplicationId/guilds/$GuildId/commands",
            DiscordApplicationCommand.serializer()
        )

    public object GuildApplicationCommandsCreate :
        Route<List<DiscordApplicationCommand>>(
            HttpMethod.Put,
            "/applications/$ApplicationId/guilds/$GuildId/commands",
            ListSerializer(DiscordApplicationCommand.serializer())
        )

    public object GuildApplicationCommandModify :
        Route<DiscordApplicationCommand>(
            HttpMethod.Patch,
            "/applications/$ApplicationId/guilds/$GuildId/commands/$CommandId",
            DiscordApplicationCommand.serializer()
        )

    public object GuildApplicationCommandGet :
        Route<DiscordApplicationCommand>(
            HttpMethod.Get,
            "/applications/$ApplicationId/guilds/$GuildId/commands/$CommandId",
            DiscordApplicationCommand.serializer()
        )

    public object GuildApplicationCommandDelete :
        Route<Unit>(HttpMethod.Delete, "/applications/$ApplicationId/guilds/$GuildId/commands/$CommandId", NoStrategy)

    public object GuildApplicationCommandPermissionsGet :
        Route<List<DiscordGuildApplicationCommandPermissions>>(
            HttpMethod.Get,
            "/applications/$ApplicationId/guilds/$GuildId/commands/permissions",
            ListSerializer(DiscordGuildApplicationCommandPermissions.serializer())
        )

    public object ApplicationCommandPermissionsGet :
        Route<DiscordGuildApplicationCommandPermissions>(
            HttpMethod.Get,
            "/applications/$ApplicationId/guilds/$GuildId/commands/$CommandId/permissions",
            DiscordGuildApplicationCommandPermissions.serializer()
        )


    /*
     * Guild Scheduled Event:
     * https://discord.com/developers/docs/resources/guild-scheduled-event
     */

    public object GuildScheduledEventsGet :
        Route<List<DiscordGuildScheduledEvent>>(
            HttpMethod.Get,
            "/guilds/$GuildId/scheduled-events",
            ListSerializer(DiscordGuildScheduledEvent.serializer())
        )

    public object GuildScheduledEventGet :
        Route<DiscordGuildScheduledEvent>(
            HttpMethod.Get,
            "/guilds/$GuildId/scheduled-events/$ScheduledEventId",
            DiscordGuildScheduledEvent.serializer()
        )

    public object GuildScheduledEventUsersGet :
        Route<List<GuildScheduledEventUsersResponse>>(
            HttpMethod.Get,
            "/guilds/$GuildId/scheduled-events/$ScheduledEventId/users",
            ListSerializer(GuildScheduledEventUsersResponse.serializer()),
        )

    public object GuildScheduledEventPatch :
        Route<DiscordGuildScheduledEvent>(
            HttpMethod.Patch,
            "/guilds/$GuildId/scheduled-events/$ScheduledEventId",
            DiscordGuildScheduledEvent.serializer()
        )

    public object GuildScheduledEventDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/scheduled-events/$ScheduledEventId", NoStrategy)

    public object GuildScheduledEventsPost :
        Route<DiscordGuildScheduledEvent>(
            HttpMethod.Post,
            "/guilds/$GuildId/scheduled-events",
            DiscordGuildScheduledEvent.serializer()
        )


    /*
     * Interactions - Receiving and Responding:
     * https://discord.com/developers/docs/interactions/receiving-and-responding
     */

    public object InteractionResponseCreate :
        Route<Unit>(
            HttpMethod.Post,
            "/interactions/$InteractionId/$InteractionToken/callback",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    public object OriginalInteractionResponseGet :
        Route<DiscordMessage>(
            HttpMethod.Get,
            "/webhooks/$ApplicationId/$InteractionToken/messages/@original",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object OriginalInteractionResponseModify :
        Route<DiscordMessage>(
            HttpMethod.Patch,
            "/webhooks/$ApplicationId/$InteractionToken/messages/@original",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object OriginalInteractionResponseDelete :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/$ApplicationId/$InteractionToken/messages/@original",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )

    public object FollowupMessageCreate :
        Route<DiscordMessage>(
            HttpMethod.Post,
            "/webhooks/$ApplicationId/$InteractionToken",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object FollowupMessageGet :
        Route<DiscordMessage>(
            HttpMethod.Get,
            "/webhooks/$ApplicationId/$InteractionToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object FollowupMessageModify :
        Route<DiscordMessage>(
            HttpMethod.Patch,
            "/webhooks/$ApplicationId/$InteractionToken/messages/$MessageId",
            DiscordMessage.serializer(),
            requiresAuthorizationHeader = false,
        )

    public object FollowupMessageDelete :
        Route<Unit>(
            HttpMethod.Delete,
            "/webhooks/$ApplicationId/$InteractionToken/messages/$MessageId",
            NoStrategy,
            requiresAuthorizationHeader = false,
        )


    /*
     * Stage Instance:
     * https://discord.com/developers/docs/resources/stage-instance
     */

    public object StageInstanceGet :
        Route<DiscordStageInstance>(HttpMethod.Get, "/stage-instances/$ChannelId", DiscordStageInstance.serializer())

    public object StageInstancePost :
        Route<DiscordStageInstance>(HttpMethod.Post, "/stage-instances", DiscordStageInstance.serializer())

    public object StageInstancePatch :
        Route<DiscordStageInstance>(HttpMethod.Patch, "/stage-instances/$ChannelId", DiscordStageInstance.serializer())

    public object StageInstanceDelete :
        Route<Unit>(HttpMethod.Delete, "/stage-instances/$ChannelId", NoStrategy)


    /*
     * Sticker:
     * https://discord.com/developers/docs/resources/sticker
     */

    public object StickerGet :
        Route<DiscordMessageSticker>(HttpMethod.Get, "/stickers/$StickerId", DiscordMessageSticker.serializer())

    public object NitroStickerPacks :
        Route<List<DiscordStickerPack>>(
            HttpMethod.Get,
            "/sticker-packs",
            ListSerializer(DiscordStickerPack.serializer())
        )

    public object GuildStickersGet :
        Route<List<DiscordMessageSticker>>(
            HttpMethod.Get,
            "/guilds/$GuildId/stickers",
            ListSerializer(DiscordMessageSticker.serializer())
        )

    public object GuildStickerGet :
        Route<DiscordMessageSticker>(
            HttpMethod.Get,
            "/guilds/$GuildId/stickers/$StickerId",
            DiscordMessageSticker.serializer()
        )

    public object GuildStickerDelete :
        Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/stickers/$StickerId", NoStrategy)

    public object GuildStickerPost :
        Route<DiscordMessageSticker>(HttpMethod.Post, "/guilds/$GuildId/stickers", DiscordMessageSticker.serializer())

    public object GuildStickerPatch :
        Route<DiscordMessageSticker>(
            HttpMethod.Patch,
            "/guilds/$GuildId/stickers/$StickerId",
            DiscordMessageSticker.serializer()
        )
}
