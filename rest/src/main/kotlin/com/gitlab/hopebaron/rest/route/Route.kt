package com.gitlab.hopebaron.rest.route

import com.gitlab.hopebaron.common.entity.*
import com.gitlab.hopebaron.rest.json.response.*
import io.ktor.http.HttpMethod
import kotlinx.serialization.Decoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.NullableSerializer
import kotlinx.serialization.internal.UnitDescriptor
import com.gitlab.hopebaron.common.entity.Emoji as EmojiEntity

sealed class Route<T>(
        val method: HttpMethod,
        val path: String,
        val strategy: DeserializationStrategy<T>
) {

    object GatewayGet
        : Route<GatewayResponse>(HttpMethod.Get, "/gateway", GatewayResponse.serializer())

    object GatewayBotGet
        : Route<GatewayResponse>(HttpMethod.Get, "/gateway/bot", GatewayResponse.serializer())

    object AuditLogGet
        : Route<AuditLogResponse>(HttpMethod.Get, "/guilds/$GuildId/audit-logs", AuditLogResponse.serializer())

    object ChannelGet
        : Route<Channel>(HttpMethod.Get, "/channels/$ChannelId", Channel.serializer())

    object ChannelPut
        : Route<Channel>(HttpMethod.Put, "/channels/$ChannelId", Channel.serializer())

    object ChannelPatch
        : Route<Channel>(HttpMethod.Patch, "/channels/$ChannelId", Channel.serializer())

    object ChannelDelete
        : Route<Channel>(HttpMethod.Delete, "/channels/$ChannelId", Channel.serializer())

    object MessageCreate
        : Route<Message>(HttpMethod.Post, "/channels/$ChannelId/messages", Message.serializer())

    object MessageGet
        : Route<Message>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId", Message.serializer())

    object MessagesGet
        : Route<List<Message>>(HttpMethod.Get, "/channels/$ChannelId/messages", ArrayListSerializer(Message.serializer()))

    object PinsGet
        : Route<List<Message>>(HttpMethod.Get, "/channels/$ChannelId/pins", ArrayListSerializer(Message.serializer()))


    object InvitesGet
        : Route<List<InviteResponse>>(HttpMethod.Get, "/channels/$ChannelId/invites", ArrayListSerializer(InviteResponse.serializer()))

    object InvitePost
        : Route<InviteResponse>(HttpMethod.Post, "/channels/$ChannelId/invites", InviteResponse.serializer())

    object ReactionPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object OwnReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/@me", NoStrategy)

    object ReactionDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji/$UserId", NoStrategy)

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
        : Route<List<User>>(HttpMethod.Get, "/channels/$ChannelId/messages/$MessageId/reactions/$Emoji", ArrayListSerializer(User.serializer()))

    object TypingIndicatorPost
        : Route<Unit>(HttpMethod.Post, "/channels/$ChannelId/typing", NoStrategy)

    object GroupDMUserDelete
        : Route<Unit>(HttpMethod.Delete, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object GroupDMUserPut
        : Route<Unit>(HttpMethod.Put, "/channels/$ChannelId/recipients/$UserId", NoStrategy)

    object EditMessagePatch
        : Route<Message>(HttpMethod.Patch, "/channels/$ChannelId/messages/$MessageId", Message.serializer())

    object GuildEmojiGet
        : Route<EmojiEntity>(HttpMethod.Get, "/guilds/$GuildId/emojis/$EmojiId", EmojiEntity.serializer())

    object GuildEmojisGet
        : Route<List<EmojiEntity>>(HttpMethod.Get, "/guilds/$GuildId/emojis", ArrayListSerializer(EmojiEntity.serializer()))

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
        : Route<User>(HttpMethod.Get, "/users/@me", User.serializer())

    object CurrentUserPatch
        : Route<User>(HttpMethod.Patch, "/users/@me", User.serializer())

    object UserGet
        : Route<User>(HttpMethod.Get, "/users/$UserId", User.serializer())

    object CurrentUsersGuildsGet
        : Route<List<Guild>>(HttpMethod.Get, "/users/@me/guilds", ArrayListSerializer(Guild.serializer()))

    object GuildLeave
        : Route<Unit>(HttpMethod.Delete, "/users/@me/guilds/$GuildId", NoStrategy)

    object DMPost
        : Route<Channel>(HttpMethod.Get, "/users/@me/channels", Channel.serializer())

    object UserConnectionsGet
        : Route<List<Connection>>(HttpMethod.Get, "/users/@me/connections", ArrayListSerializer(Connection.serializer()))

    object GuildPost
        : Route<Guild>(HttpMethod.Post, "/guilds", Guild.serializer())

    object GuildGet
        : Route<Guild>(HttpMethod.Get, "/guilds/$GuildId", Guild.serializer())

    object GuildPatch
        : Route<Guild>(HttpMethod.Patch, "/guilds/$GuildId", Guild.serializer())

    object GuildDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId", NoStrategy)

    object GuildChannelsGet
        : Route<List<Channel>>(HttpMethod.Get, "/guilds/$GuildId/channels", ArrayListSerializer(Channel.serializer()))

    object GuildChannelsPost
        : Route<Channel>(HttpMethod.Post, "/guilds/$GuildId/channels", Channel.serializer())


    object GuildChannelsPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/channels", NoStrategy)

    object GuildMemberGet
        : Route<GuildMember>(HttpMethod.Get, "/guilds/$GuildId/members/$UserId", GuildMember.serializer())

    object GuildMembersGet
        : Route<List<GuildMember>>(HttpMethod.Get, "/guilds/$GuildId/members", ArrayListSerializer(GuildMember.serializer()))


    object GuildMemberPut
        : Route<GuildMember?>(HttpMethod.Put, "/guilds/$GuildId/members/$UserId", NullableSerializer(GuildMember.serializer()))


    object GuildMemberPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/members/$UserId", NoStrategy)

    object GuildCurrentUserNickPatch
        : Route<Unit>(HttpMethod.Patch, "/guilds/$GuildId/members/@me/nick", NoStrategy)

    object GuildMemberRolePut
        : Route<Unit>(HttpMethod.Put, "/guilds/$GuildId/members/$UserId/roles/$RoleId", NoStrategy)

    object GuildMemberRoleDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/members/$UserId/roles/$RoleId", NoStrategy)


    object GuildMemberDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/members/$UserId", NoStrategy)

    object GuildBansGet
        : Route<List<GuildBan>>(HttpMethod.Get, "/guilds/$GuildId/bans", ArrayListSerializer(GuildBan.serializer()))


    object GuildBanGet
        : Route<GuildBan>(HttpMethod.Get, "/guilds/$GuildId/bans/$UserId", GuildBan.serializer())

    object GuildBanPut
        : Route<Unit>(HttpMethod.Put, "/guilds/$GuildId/bans/$UserId", NoStrategy)

    object GuildBanDelete
        : Route<Unit>(HttpMethod.Delete, "/guilds/$GuildId/bans/$UserId", NoStrategy)

    object GuildRolesGet
        : Route<List<Role>>(HttpMethod.Get, "/guilds/$GuildId/roles", ArrayListSerializer(Role.serializer()))

    object GuildRolePost
        : Route<Role>(HttpMethod.Post, "/guilds/$GuildId/roles", Role.serializer())

    object GuildRolesPatch
        : Route<Role>(HttpMethod.Patch, "/guilds/$GuildId/roles", Role.serializer())

    object GuildRolePatch
        : Route<Role>(HttpMethod.Patch, "/guilds/$GuildId/roles/$RoleId", Role.serializer())

    object GuildRoleDelete
        : Route<Role>(HttpMethod.Delete, "/guilds/$GuildId/roles/$RoleId", Role.serializer())


    object GuildPruneCountGet
        : Route<PruneResponse>(HttpMethod.Get, "/guilds/$GuildId/prune", PruneResponse.serializer())

    object GuildPrunePost
        : Route<PruneResponse>(HttpMethod.Post, "/guilds/$GuildId/prune", PruneResponse.serializer())

    object GuildVoiceRegionsGet
        : Route<List<VoiceRegion>>(HttpMethod.Get, "/guilds/$GuildId/regions", ArrayListSerializer(VoiceRegion.serializer()))

    object GuildInvitesGet
        : Route<List<InviteResponse>>(HttpMethod.Get, "/guilds/$GuildId/invites", ArrayListSerializer(InviteResponse.serializer()))

    object GuildIntegrationGet
        : Route<List<GuildIntegrations>>(HttpMethod.Get, "/guilds/$GuildId/integrations", ArrayListSerializer(GuildIntegrations.serializer()))

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
        : Route<List<Webhook>>(HttpMethod.Get, "/channels/$ChannelId/webhooks", ArrayListSerializer(Webhook.serializer()))

    object GuildWebhooksGet
        : Route<List<Webhook>>(HttpMethod.Get, "/guild/$GuildId/webhooks", ArrayListSerializer(Webhook.serializer()))

    object WebhookGet
        : Route<Webhook>(HttpMethod.Get, "/webhooks/$WebhookId", Webhook.serializer())

    object WebhookPost
        : Route<Webhook>(HttpMethod.Post, "/channels/$ChannelId/webhooks", Webhook.serializer())

    object WebhookByTokenGet
        : Route<Webhook>(HttpMethod.Get, "/webhooks/$WebhookId/$WebhookToken", Webhook.serializer())

    object WebhookPatch
        : Route<Webhook>(HttpMethod.Patch, "/webhooks/$WebhookId", Webhook.serializer())

    object WebhookByTokenPatch
        : Route<Webhook>(HttpMethod.Patch, "/webhooks/$WebhookId/$WebhookToken", Webhook.serializer())

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
        : Route<List<VoiceRegion>>(HttpMethod.Get, "/voice/regions", ArrayListSerializer(VoiceRegion.serializer()))

    companion object {
        const val baseUrl = "https://discordapp.com/api/v6"
    }

    open class Key(val identifier: String) {
        override fun toString(): String = identifier
    }

    object GuildId : Key("{guild.id}")
    object ChannelId : Key("{channel.id}")
    object MessageId : Key("{message.id}")
    object Emoji : Key("{emoji}")
    object UserId : Key("{user.id}")
    object OverwriteId : Key("{overwrite.id}")
    object EmojiId : Key("{emoji.id}")
    object InviteCode : Key("{invite.code}")
    object RoleId : Key("{role.id}")
    object IntegrationId : Key("{integration.id}")
    object WebhookId : Key("{webhook.id}")
    object WebhookToken : Key("{webhook.token}")

}

internal object NoStrategy : DeserializationStrategy<Unit> {
    override val descriptor: SerialDescriptor
        get() = UnitDescriptor

    override fun deserialize(decoder: Decoder) {}

    override fun patch(decoder: Decoder, old: Unit) {}
}
