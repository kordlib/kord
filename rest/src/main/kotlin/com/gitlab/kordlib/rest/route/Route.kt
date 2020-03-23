package com.gitlab.kordlib.rest.route

import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.rest.json.response.*
import io.ktor.http.HttpMethod
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import com.gitlab.kordlib.common.entity.DiscordEmoji as EmojiEntity

sealed class Route<T>(
        val method: HttpMethod,
        val path: String,
        val strategy: DeserializationStrategy<T>
) {

    override fun toString(): String = "Route(method:${method.value},path:$path,strategy:${strategy.descriptor.serialName})"

    object GatewayGet
        : Route<GatewayResponse>(HttpMethod.Get, "/gateway", GatewayResponse.serializer())

    object GatewayBotGet
        : Route<BotGatewayResponse>(HttpMethod.Get, "/gateway/bot", BotGatewayResponse.serializer())

    object AuditLogGet
        : Route<AuditLogResponse>(HttpMethod.Get, "/guilds/$GuildId/audit-logs", AuditLogResponse.serializer())

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
        : Route<List<DiscordMessage>>(HttpMethod.Get, "/channels/$ChannelId/messages", ListSerializer(DiscordMessage.serializer()))

    object PinsGet
        : Route<List<DiscordMessage>>(HttpMethod.Get, "/channels/$ChannelId/pins", ListSerializer(DiscordMessage.serializer()))

    object InvitesGet
        : Route<List<InviteResponse>>(HttpMethod.Get, "/channels/$ChannelId/invites", ListSerializer(InviteResponse.serializer()))

    object InvitePost
        : Route<InviteResponse>(HttpMethod.Post, "/channels/$ChannelId/invites", InviteResponse.serializer())

    object ReactionPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object OwnReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object ReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/$UserId", NoStrategy)

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
        : Route<List<DiscordUser>>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji", ListSerializer(DiscordUser.serializer()))

    object TypingIndicatorPost
        : Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/typing", NoStrategy)

    object GroupDMUserDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object GroupDMUserPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object EditMessagePatch
        : Route<DiscordMessage>(HttpMethod.Patch, "/channels/$ChannelId/messages/$MessageId", DiscordMessage.serializer())

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
        : Route<InviteResponse>(HttpMethod.Get, "/invites/$InviteCode", InviteResponse.serializer())

    object InviteDelete
        : Route<InviteResponse>(HttpMethod.Delete, "/invites/$InviteCode", InviteResponse.serializer())

    object CurrentUserGet
        : Route<DiscordUser>(HttpMethod.Get, "/users/@me", DiscordUser.serializer())

    object CurrentUserPatch
        : Route<DiscordUser>(HttpMethod.Patch, "/users/@me", DiscordUser.serializer())

    object UserGet
        : Route<DiscordUser>(HttpMethod.Get, "/users/$UserId", DiscordUser.serializer())

    object CurrentUsersGuildsGet
        : Route<List<DiscordPartialGuild>>(HttpMethod.Get, "/users/@me/guilds", ListSerializer(DiscordPartialGuild.serializer()))

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
        : Route<List<DiscordChannel>>(HttpMethod.Get, "/guilds/$GuildId/channels", ListSerializer(DiscordChannel.serializer()))

    object GuildChannelsPost
        : Route<DiscordChannel>(HttpMethod.Post, "/guilds/$GuildId/channels", DiscordChannel.serializer())

    object GuildChannelsPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/channels", NoStrategy)

    object GuildMemberGet
        : Route<DiscordGuildMember>(HttpMethod.Get, "/guilds/$GuildId/members/$UserId", DiscordGuildMember.serializer())

    object GuildMembersGet
        : Route<List<DiscordGuildMember>>(HttpMethod.Get, "/guilds/$GuildId/members", ListSerializer(DiscordGuildMember.serializer()))

    object GuildMemberPut
        : Route<DiscordGuildMember?>(HttpMethod.Put, "/guilds/$GuildId/members/$UserId", DiscordGuildMember.serializer().nullable)

    object GuildMemberPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/members/$UserId", NoStrategy)

    object GuildCurrentUserNickPatch
        : Route<String>(HttpMethod.Patch, "/guilds/$GuildId/members/@me/nick", String.serializer())

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
        : Route<List<VoiceRegion>>(HttpMethod.Get, "/guilds/$GuildId/regions", ListSerializer(VoiceRegion.serializer()))

    object GuildInvitesGet
        : Route<List<InviteResponse>>(HttpMethod.Get, "/guilds/$GuildId/invites", ListSerializer(InviteResponse.serializer()))

    object GuildIntegrationGet
        : Route<List<IntegrationResponse>>(HttpMethod.Get, "/guilds/$GuildId/integrations", ListSerializer(IntegrationResponse.serializer()))

    object GuildIntegrationPost
        : Route<Unit>(HttpMethod.Post, "/guilds/$GuildId/integrations", NoStrategy)

    object GuildIntegrationPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/integrations/$IntegrationId", NoStrategy)

    object GuildIntegrationDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/integrations/$IntegrationId", NoStrategy)

    object GuildIntegrationSyncPost
        : Route<Unit>(HttpMethod.Post, "/guilds/$GuildId/integrations/$IntegrationId/sync", NoStrategy)

    object GuildEmbedGet
        : Route<EmbedResponse>(HttpMethod.Get, "/guilds/$GuildId/embed", EmbedResponse.serializer())

    object GuildEmbedPatch
        : Route<EmbedResponse>(HttpMethod.Patch, "/guilds/$GuildId/embed", EmbedResponse.serializer())

    object GuildVanityInviteGet
        : Route<InviteResponse>(HttpMethod.Get, "/guilds/$GuildId/vanity-url", InviteResponse.serializer())

    //TODO must return an image
    object GuildWidgetGet
        : Route<Unit>(HttpMethod.Get, "/guilds/$GuildId/widget", NoStrategy)

    object ChannelWebhooksGet
        : Route<List<DiscordWebhook>>(HttpMethod.Get, "/channels/$ChannelId/webhooks", ListSerializer(DiscordWebhook.serializer()))

    object GuildWebhooksGet
        : Route<List<DiscordWebhook>>(HttpMethod.Get, "/guild/$GuildId/webhooks", ListSerializer(DiscordWebhook.serializer()))

    object WebhookGet
        : Route<DiscordWebhook>(HttpMethod.Get, "/webhooks/$WebhookId", DiscordWebhook.serializer())

    object WebhookPost
        : Route<DiscordWebhook>(HttpMethod.Post, "/channels/$ChannelId/webhooks", DiscordWebhook.serializer())

    object WebhookByTokenGet
        : Route<DiscordWebhook>(HttpMethod.Get, "/webhooks/$WebhookId/$WebhookToken", DiscordWebhook.serializer())

    object WebhookPatch
        : Route<DiscordWebhook>(HttpMethod.Patch, "/webhooks/$WebhookId", DiscordWebhook.serializer())

    object WebhookByTokenPatch
        : Route<DiscordWebhook>(HttpMethod.Patch, "/webhooks/$WebhookId/$WebhookToken", DiscordWebhook.serializer())

    object WebhookDelete
        : Route<Unit>(HttpMethod.Delete, "/webhooks/$WebhookId", NoStrategy)

    object WebhookByTokenDelete
        : Route<Unit>(HttpMethod.Delete, "/webhooks/$WebhookId/$WebhookToken", NoStrategy)

    //TODO Make sure of the return of these routes below

    object ExecuteWebhookPost
        : Route<Unit>(HttpMethod.Post, "/webhooks/$WebhookId/$WebhookToken", NoStrategy)

    object ExecuteSlackWebhookPost
        : Route<Unit>(HttpMethod.Post, "/webhooks/$WebhookId/$WebhookToken/slack", NoStrategy)

    object ExecuteGithubWebhookPost
        : Route<Unit>(HttpMethod.Post, "/webhooks/$WebhookId/$WebhookToken", NoStrategy)

    object VoiceRegionsGet
        : Route<List<VoiceRegion>>(HttpMethod.Get, "/voice/regions", ListSerializer(VoiceRegion.serializer()))

    object CurrentApplicationInfo
        : Route<ApplicationInfoResponse>(HttpMethod.Get, "/oauth2/applications/@me", ApplicationInfoResponse.serializer())

    companion object {
        const val baseUrl = "https://discordapp.com/api/v6"
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

}

internal object NoStrategy : DeserializationStrategy<Unit> {
    override val descriptor: SerialDescriptor
        get() = SerialDescriptor("NoStrategy", StructureKind.OBJECT)

    override fun deserialize(decoder: Decoder) {}

    override fun patch(decoder: Decoder, old: Unit) {}
}
