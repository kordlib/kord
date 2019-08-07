package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
        val id: String,
        var name: String? = null,
        var color: Int? = null,
        var hoist: Boolean? = null,
        var position: Int? = null,
        var permissions: Permissions? = null,
        var managed: Boolean? = null,
        var mentionable: Boolean? = null,
        @SerialName("guild_id")
        var guildId: String? = null
) {
    companion object {
        fun from(entity: Role) = with(entity) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: DeletedGuildRole) = with(entity) { RoleData(id, guildId) }
        fun from(entity: AuditLogRoleChange) = with(entity) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: GuildRole) = with(entity.role) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable, entity.guildId) }

    }
}


@Serializable
data class ChannelData(
        val id: String,
        var type: ChannelType,
        @SerialName("guild_id")
        var guildId: String? = null,
        var position: Int? = null,
        @SerialName("permission_overwrites")
        var permissionOverwrites: List<Overwrite>? = null,
        var name: String? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        @SerialName("last_message_id")
        var lastMessageId: String? = null,
        var bitrate: Int? = null,
        @SerialName("user_limit")
        var userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        var rateLimitPerUser: Int? = null,
        var recipients: List<User>? = null,
        var icon: String? = null,
        @SerialName("owner_id")
        var ownerId: String? = null,
        @SerialName("application_id")
        var applicationId: String? = null,
        @SerialName("parent_id")
        var parentId: String? = null,
        @SerialName("last_pin_timestamp")
        var lastPinTimestamp: String? = null
) {
    companion object {
        fun from(entity: Channel) = with(entity) {
            ChannelData(
                    id, type, guildId, position, permissionOverwrites, name, topic, nsfw, lastMessageId, bitrate, userLimit,
                    rateLimitPerUser, recipients, icon, ownerId, applicationId, parentId, lastPinTimestamp
            )
        }
    }
}


@Serializable
data class GuildData(
        val id: String,
        var name: String? = null,
        var icon: String? = null,
        var splash: String? = null,
        var owner: Boolean? = null,
        @SerialName("owner_id")
        var ownerId: String? = null,
        var permissions: Permissions? = null,
        var region: String? = null,
        @SerialName("afk_channel_id")
        var afkChannelId: String? = null,
        @SerialName("afk_timeout")
        var afkTimeout: Int? = null,
        @SerialName("embed_enabled")
        var embedEnabled: Boolean? = null,
        @SerialName("embed_channel_id")
        var embedChannelId: String? = null,
        @SerialName("verification_level")
        var verificationLevel: VerificationLevel? = null,
        @SerialName("default_message_notifications")
        var defaultMessageNotifications: DefaultMessageNotificationLevel? = null,
        @SerialName("explicit_content_filter")
        var explicitContentFilter: ExplicitContentFilter? = null,
        var roles: List<Role>? = null,
        var emojis: List<Emoji>? = null,
        var features: List<String>? = null,
        @SerialName("mfa_level")
        var mfaLevel: MFALevel? = null,
        @SerialName("application_id")
        var applicationId: String? = null,
        @SerialName("widget_enabled")
        var widgetEnabled: Boolean? = null,
        @SerialName("widget_channel_id")
        var widgetChannelId: String? = null,
        @SerialName("system_channel_id")
        var systemChannelId: String? = null,
        @SerialName("joined_at")
        var joinedAt: String? = null,
        var large: Boolean? = null,
        var unavailable: Boolean? = null,
        @SerialName("member_count")
        var memberCount: Int? = null,
        @SerialName("voice_states")
        var voiceStates: List<VoiceState>? = null,
        var members: List<GuildMember>? = null,
        var channels: List<Channel>? = null,
        var presences: List<PresenceUpdateData>? = null,
        @SerialName("max_presences")
        var maxPresences: Int? = null,
        @SerialName("max_members")
        var maxMembers: Int? = null,
        @SerialName("vanity_url_code")
        var vanityUrlCode: String? = null,
        var description: String? = null,
        var banner: String? = null
) {
    companion object {
        fun from(entity: UnavailableGuild) = with(entity) { GuildData(id, unavailable = unavailable) }
        fun from(entity: PartialGuild) = with(entity) { GuildData(id, name, icon, owner = owner, permissions = permissions) }
        fun from(entity: Guild) = with(entity) {
            GuildData(
                    id, name, icon, splash, owner, ownerId, permissions, region,
                    afkChannelId, afkTimeout, embedEnabled, embedChannelId, verificationLevel, defaultMessageNotifications,
                    explicitContentFilter, roles, emojis, features, mfaLevel, applicationId, widgetEnabled,
                    widgetChannelId, systemChannelId, joinedAt, large, unavailable, memberCount, voiceStates, members,
                    channels, presences, maxPresences, maxMembers, vanityUrlCode, description, banner
            )
        }

    }
}


@Serializable
data class GuildIntegrationsData(
        val id: String,
        var name: String,
        var type: String,
        var enabled: Boolean,
        var syncing: Boolean,
        @SerialName("role_id")
        var roleId: String,
        @SerialName("expire_behavior")
        var expireBehavior: Int,
        @SerialName("expire_grace_period")
        var gracePeriod: Int,
        var user: User,
        var account: IntegrationAccount,
        @SerialName("synced_at")
        var syncedAt: String
) {
    companion object {
        fun from(entity: GuildIntegrations) = with(entity) {
            GuildIntegrationsData(
                    id, name, type, enabled, syncing, roleId, expireBehavior, gracePeriod, user, account, syncedAt
            )
        }

    }
}


@Serializable
data class GuildMemberData(
        var user: User? = null,
        var nick: String? = null,
        var roles: List<String>? = null,
        @SerialName("joined_at")
        var joinedAt: String? = null,
        var deaf: Boolean? = null,
        var mute: Boolean? = null,
        @SerialName("guild_id")
        var guildId: String? = null
) {
    companion object {

        fun from(entity: GuildMember) = with(entity) { GuildMemberData(user, nick, roles, joinedAt, deaf, mute) }
        fun from(entity: AddedGuildMember) = with(entity) { GuildMemberData(user, nick, roles, joinedAt, deaf, mute, guildId) }
        fun from(entity: RemovedGuildMember) = with(entity) { GuildMemberData(user, guildId = guildId) }
        fun from(entity: PartialGuildMember) = with(entity) { GuildMemberData(null, nick, roles, joinedAt, deaf, mute) }

    }
}


@Serializable
data class UserData(
        val id: String,
        var username: String,
        var discriminator: String,
        var avatar: String? = null,
        var bot: Boolean? = null,
        @SerialName("mfa_enable")
        var mfaEnable: Boolean? = null,
        var locale: String? = null,
        var flags: Int? = null,
        @SerialName("premium_type")
        var premiumType: Premium? = null,
        var verified: Boolean? = null,
        var email: String? = null
) {
    companion object {
        fun from(entity: User) = with(entity) {
            UserData(id, username, discriminator, avatar, bot, mfaEnable, locale, flags,
                    premiumType, verified, email)
        }

    }
}


@Serializable
data class MessageData(
        val id: String,
        @SerialName("channel_id")
        var channelId: String,
        @SerialName("guild_id")
        var guildId: String? = null,
        var author: User? = null,
        var member: PartialGuildMember? = null,
        var content: String? = null,
        var timestamp: String? = null,
        @SerialName("edited_timestamp")
        var editedTimestamp: String? = null,
        var tts: Boolean? = null,
        @SerialName("mention_everyone")
        var mentionEveryone: Boolean? = null,
        var mentions: List<OptionallyMemberUser>? = null,
        @SerialName("mention_roles")
        var mentionRoles: List<Role>? = null,
        var attachments: List<Attachment>? = null,
        var embeds: List<Embed>? = null,
        var reactions: List<Reaction>? = null,
        var nonce: String? = null,
        var pinned: Boolean? = null,
        @SerialName("webhook_id")
        var webhookId: String? = null,
        var type: MessageType? = null,
        var activity: MessageActivity? = null,
        var application: MessageApplication? = null
) {
    companion object {
        fun from(entity: Message) = with(entity) {
            MessageData(
                    id, channelId, guildId, author, member, content, timestamp,
                    editedTimestamp, tts, mentionEveryone, mentions,
                    mentionRoles, attachments, embeds, reactions, nonce, pinned,
                    webhookId, type, activity, application
            )
        }
    }
}


@Serializable
data class OverwriteData(
        val id: String,
        var type: String,
        var allow: Int,
        var deny: Int
) {
    companion object {
        fun from(entity: Overwrite) = with(entity) { OverwriteData(id, type, allow, deny) }

    }
}


@Serializable
data class EmojiData(
        val id: String? = null,
        var name: String,
        var roles: List<String>? = null,
        var user: User? = null,
        @SerialName("require_colons")
        var requireColons: Boolean? = null,
        var managed: Boolean? = null,
        var animated: Boolean? = null
) {
    companion object {
        fun from(entity: Emoji) = with(entity) { EmojiData(id, name, roles, user, requireColons, managed, animated) }
        fun from(entity: PartialEmoji) = with(entity) { EmojiData(id, name) }

    }
}


@Serializable
data class WebhookData(
        val id: String,
        @SerialName("guild_id")
        var guildId: String? = null,
        var channelId: String,
        var user: User? = null,
        var name: String? = null,
        var avatar: String? = null,
        var token: String
) {
    companion object {
        fun from(entity: Webhook) = with(entity) { WebhookData(id, guildId, channelId, user, name, avatar, token) }
    }
}